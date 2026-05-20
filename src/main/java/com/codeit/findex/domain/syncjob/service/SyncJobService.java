package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import com.codeit.findex.infra.openapi.OpenApiClient;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncJobService {

    private final SyncJobRepository syncJobRepository;
    private final OpenApiClient openApiClient;
    private final IndexInfoRepository indexInfoRepository;

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
                OpenApiResponseDto response = openApiClient.getStockMarketIndex(
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

}
