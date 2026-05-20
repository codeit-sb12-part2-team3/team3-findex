package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import com.codeit.findex.infra.openapi.OpenApiClient;
import com.codeit.findex.infra.openapi.OpenApiService;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncJobService {

    private final SyncJobRepository syncJobRepository;

    private final OpenApiClient openApiClient;

    private final OpenApiService openApiService;

    private final IndexInfoRepository indexInfoRepository;

    private final IndexDataRepository indexDataRepository;

    public Slice<SyncJobListResponse> getSyncJobList(
            SyncJobSearchCondition condition, LocalDateTime lastJobTime, UUID lastId, int size
    ) {


        Sort sort = Sort.by(
                Sort.Order.desc("jobTime"),
                Sort.Order.desc("id")
        );

        Pageable pageable = PageRequest.of(0, size, sort);

        Specification<SyncJob> spec = SyncJobSpecification
                .withCondition(condition)
                .and(SyncJobSpecification.cursor(lastJobTime, lastId));

        Slice<SyncJob> slice = syncJobRepository.findAll(spec, pageable);

        return slice.map(SyncJobListResponse::from);

    }

    @Transactional
    public List<SyncJobListResponse> syncIndexInfo(
            List<String> indexNames,
            String workerIp
    ) {
        List<SyncJobListResponse> responses = new ArrayList<>();

        for (String indexName : indexNames) {
            SyncJob syncJob;

            try {
                com.codeit.findex.infra.openapi.dto.OpenApiResponseDto response = openApiClient.getStockMarketIndex(
                        indexName,
                        null,
                        1,
                        1
                );

                OpenApiIndexItemDto item = response.getResponse()
                        .getBody()
                        .getItems()
                        .getItem()
                        .get(0);

                IndexInfo indexInfo = indexInfoRepository
                        .findByIndexName(item.getIdxNm())
                        .orElseGet(() -> IndexInfo.builder()
                                .indexName(item.getIdxNm())
                                .indexClassification(item.getIdxCsf())
                                .employedItemsCount(Integer.valueOf(item.getEpyItmsCnt()))
                                .sourceType("OPEN_API")
                                .favorite(false)
                                .build()
                        );

                indexInfo.update(
                        item.getIdxCsf(),
                        Integer.valueOf(item.getEpyItmsCnt())
                );

                indexInfoRepository.save(indexInfo);

                syncJob = SyncJob.builder()
                        .indexInfo(indexInfo)
                        .jobType("지수 정보")
                        .targetDate(null)
                        .worker(workerIp)
                        .jobTime(LocalDateTime.now())
                        .result("성공")
                        .build();

            } catch (Exception e) {
                IndexInfo indexInfo = indexInfoRepository
                        .findByIndexName(indexName)
                        .orElseGet(() -> IndexInfo.builder()
                                .indexName(indexName)
                                .indexClassification("UNKNOWN")
                                .employedItemsCount(0)
                                .sourceType("OPEN_API")
                                .favorite(false)
                                .build()
                        );

                indexInfoRepository.save(indexInfo);

                syncJob = SyncJob.builder()
                        .indexInfo(indexInfo)
                        .jobType("지수 정보")
                        .targetDate(null)
                        .worker(workerIp)
                        .jobTime(LocalDateTime.now())
                        .result("실패")
                        .build();
            }

            syncJobRepository.save(syncJob);
            responses.add(SyncJobListResponse.from(syncJob));
        }

        return responses;

    }

    @Transactional
    public List<SyncJobListResponse> syncIndexData(
            UUID indexId,
            LocalDate startDate,
            LocalDate endDate,
            String workerIp
    ) {
        validateDateRange(startDate, endDate);

        List<IndexInfo> targetIndexInfos = findTargetIndexInfos(indexId);
        List<SyncJobListResponse> responses = new ArrayList<>();

        for (IndexInfo indexInfo : targetIndexInfos) {
            LocalDate targetDate = startDate;

            while (!targetDate.isAfter(endDate)){
                SyncJob syncJob;

                try {
                    String baseDate = targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);

                    openApiService.syncAndSaveIndexData(
                            indexInfo.getIndexName(),
                            baseDate,
                            1,
                            100
                    );

                    syncJob = SyncJob.builder()
                            .indexInfo(indexInfo)
                            .jobType("지수 데이터")
                            .targetDate(targetDate)
                            .worker(workerIp)
                            .jobTime(LocalDateTime.now())
                            .result("성공")
                            .build();

                } catch (Exception e) {
                    syncJob = SyncJob.builder()
                            .indexInfo(indexInfo)
                            .jobType("지수 데이터")
                            .targetDate(targetDate)
                            .worker(workerIp)
                            .jobTime(LocalDateTime.now())
                            .result("실패")
                            .build();
                }

                syncJobRepository.save(syncJob);
                responses.add(SyncJobListResponse.from(syncJob));

                targetDate = targetDate.plusDays(1);
            }
        }

        return responses;
    }

    private List<IndexInfo> findTargetIndexInfos(UUID indexId){
        if(indexId != null){
            IndexInfo indexInfo = indexInfoRepository.findById(indexId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지수입니다."));
            return List.of(indexInfo);
        }
        return indexInfoRepository.findAll();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("대상 날짜는 반드시 지정해야 합니다.");
        }
        if (startDate.isAfter(endDate)){
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }

    }

}
