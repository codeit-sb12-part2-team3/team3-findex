package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import com.codeit.findex.infra.openapi.service.OpenApiService;
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

    private static final String INDEX_INFO_JOB = "지수 정보";
    private static final String INDEX_DATA_JOB = "지수 데이터";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";

    private final SyncJobRepository syncJobRepository;
    private final OpenApiService openApiService;
    private final IndexInfoRepository indexInfoRepository;

    public Slice<SyncJobListResponse> getSyncJobList(
            SyncJobSearchCondition condition,
            LocalDateTime lastJobTime,
            UUID lastId,
            String sortField,
            String sortDirection,
            int size
    ) {
        String safeSortField = switch (sortField) {
            case "targetDate" -> "targetDate";
            case "jobTime" -> "jobTime";
            default -> "jobTime";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(
                new Sort.Order(direction, safeSortField),
                new Sort.Order(direction, "id")
        );
        Pageable pageable = PageRequest.of(0, size, sort);
        Specification<SyncJob> spec = SyncJobSpecification
                .withCondition(condition)
                .and(SyncJobSpecification.cursor(lastJobTime, lastId));

        return syncJobRepository.findAll(spec, pageable)
                .map(SyncJobListResponse::from);
    }

    public List<SyncJobListResponse> syncIndexInfo(String workerIp) {
        List<IndexInfo> indexInfos = findAllIndexInfos();

        if (indexInfos.isEmpty()) {
            indexInfos = initializeIndexInfos();
        }

        List<SyncJobListResponse> responses = new ArrayList<>();

        for (IndexInfo indexInfo : indexInfos) {
            SyncJob syncJob = syncIndexInfo(indexInfo, workerIp);
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
                SyncJob syncJob = syncIndexData(indexInfo, targetDate, workerIp);
                responses.add(SyncJobListResponse.from(syncJob));
                targetDate = targetDate.plusDays(1);
            }
        }

        return responses;
    }

    private List<IndexInfo> initializeIndexInfos() {
        try {
            openApiService.syncAndSaveAllIndexInfo();
            return findAllIndexInfos();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private SyncJob syncIndexInfo(IndexInfo indexInfo, String workerIp) {
        try {
            openApiService.syncAndSaveIndexInfo(indexInfo.getIndexName());
            return saveSyncJob(indexInfo, INDEX_INFO_JOB, null, workerIp, SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return saveSyncJob(indexInfo, INDEX_INFO_JOB, null, workerIp, FAILED);
        }
    }

    private SyncJob syncIndexData(
            IndexInfo indexInfo,
            LocalDate targetDate,
            String workerIp
    ) {
        try {
            String baseDate = targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);
            openApiService.syncAndSaveIndexData(
                    indexInfo.getIndexName(),
                    baseDate,
                    1,
                    100
            );
            return saveSyncJob(indexInfo, INDEX_DATA_JOB, targetDate, workerIp, SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return saveSyncJob(indexInfo, INDEX_DATA_JOB, targetDate, workerIp, FAILED);
        }
    }

    private SyncJob saveSyncJob(
            IndexInfo indexInfo,
            String jobType,
            LocalDate targetDate,
            String workerIp,
            String result
    ) {
        SyncJob syncJob = SyncJob.builder()
                .indexInfo(indexInfo)
                .jobType(jobType)
                .targetDate(targetDate)
                .worker(workerIp)
                .jobTime(LocalDateTime.now())
                .result(result)
                .build();

        return syncJobRepository.save(syncJob);
    }

    private List<IndexInfo> findAllIndexInfos() {
        return indexInfoRepository.findAll();
    }

    private List<IndexInfo> findTargetIndexInfos(List<String> indexInfoIds) {
        if (indexInfoIds == null || indexInfoIds.isEmpty() || indexInfoIds.contains("ALL")) {
            return findAllIndexInfos();
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