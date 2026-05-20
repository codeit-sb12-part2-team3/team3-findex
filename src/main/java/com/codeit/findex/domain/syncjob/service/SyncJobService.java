package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import com.codeit.findex.infra.openapi.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncJobService {

    private final SyncJobRepository syncJobRepository;
    private final OpenApiService openApiService;
    private final IndexInfoRepository indexInfoRepository;

    public Slice<SyncJobListResponse> getSyncJobList(
            SyncJobSearchCondition condition,
            LocalDateTime lastJobTime,
            UUID lastId,
            int size
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

    public List<SyncJobListResponse> syncIndexInfo(String workerIp) {
        List<IndexInfo> allIndexInfos = indexInfoRepository.findAll();
        List<SyncJobListResponse> responses = new ArrayList<>();

        if (allIndexInfos.isEmpty()) {
            try {
                openApiService.syncAndSaveAllIndexInfo();
                allIndexInfos = indexInfoRepository.findAll();
            } catch (Exception e) {
                e.printStackTrace();
                return responses;
            }
        }

        for (IndexInfo indexInfo : allIndexInfos) {
            SyncJob syncJob;
            try {
                openApiService.syncAndSaveIndexInfo(indexInfo.getIndexName());
                syncJob = createSyncJob(indexInfo, "지수 정보", null, workerIp, "SUCCESS");
            } catch (Exception e) {
                e.printStackTrace();
                syncJob = createSyncJob(indexInfo, "지수 정보", null, workerIp, "FAIL");
            }
            syncJob = syncJobRepository.save(syncJob);
            responses.add(SyncJobListResponse.from(syncJob));
        }
        return responses;
    }

    public List<SyncJobListResponse> syncIndexData(
            List<String> indexInfoIds,
            LocalDate baseDateFrom,
            LocalDate baseDateTo,
            String workerIp
    ) {
        validateDateRange(baseDateFrom, baseDateTo);

        List<IndexInfo> targetIndexInfos = findTargetIndexInfos(indexInfoIds);
        List<SyncJobListResponse> responses = new ArrayList<>();

        for (IndexInfo indexInfo : targetIndexInfos) {
            LocalDate targetDate = baseDateFrom;

            while (!targetDate.isAfter(baseDateTo)) {
                SyncJob syncJob;
                try {
                    String baseDate = targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);
                    openApiService.syncAndSaveIndexData(
                            indexInfo.getIndexName(), baseDate, 1, 100
                    );
                    syncJob = createSyncJob(indexInfo, "지수 데이터", targetDate, workerIp, "SUCCESS");
                } catch (Exception e) {
                    e.printStackTrace();
                    syncJob = createSyncJob(indexInfo, "지수 데이터", targetDate, workerIp, "FAILED");
                }
                syncJob = syncJobRepository.save(syncJob);
                responses.add(SyncJobListResponse.from(syncJob));
                targetDate = targetDate.plusDays(1);
            }
        }
        return responses;
    }

    private SyncJob createSyncJob(
            IndexInfo indexInfo, String jobType, LocalDate targetDate,
            String workerIp, String result
    ) {
        return SyncJob.builder()
                .indexInfo(indexInfo)
                .jobType(jobType)
                .targetDate(targetDate)
                .worker(workerIp)
                .jobTime(LocalDateTime.now())
                .result(result)
                .build();
    }

    private List<IndexInfo> findTargetIndexInfos(List<String> indexInfoIds) {
        if (indexInfoIds == null || indexInfoIds.isEmpty() || indexInfoIds.contains("ALL")) {
            return indexInfoRepository.findAll();
        }
        return indexInfoIds.stream()
                .map(id -> indexInfoRepository.findById(UUID.fromString(id))
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지수입니다.")))
                .toList();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("대상 날짜는 반드시 지정해야 합니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }
    }
}