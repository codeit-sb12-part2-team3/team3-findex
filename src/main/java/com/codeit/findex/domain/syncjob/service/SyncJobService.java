package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.syncjob.dto.CursorPageResponseSyncJobDto;
import com.codeit.findex.domain.syncjob.dto.SyncJobDetailResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import com.codeit.findex.infra.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public CursorPageResponseSyncJobDto getSyncJobList(
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

        // Slice -> Page로 변경
        Page<SyncJob> page = syncJobRepository.findAll(spec, pageable);

        List<SyncJobDetailResponse> content = page.getContent().stream()
                .map(SyncJobDetailResponse::from)
                .toList();

        String nextCursor = null;
        UUID newNextIdAfter = null;

        if (page.hasNext() && !content.isEmpty()) {
            SyncJobDetailResponse lastItem = content.get(content.size() - 1);
            nextCursor = lastItem.jobTime() != null ? lastItem.jobTime().toString() : null;
            newNextIdAfter = lastItem.id();
        }

        return new CursorPageResponseSyncJobDto(
                content,
                nextCursor,
                newNextIdAfter,
                content.size(),
                page.getTotalElements(), // 전체 개수 세팅
                page.hasNext()
        );
    }

    public List<SyncJobDetailResponse> syncIndexInfo(String workerIp) {
        List<IndexInfo> indexInfos = findAllIndexInfos();

        if (indexInfos.isEmpty()) {
            indexInfos = initializeIndexInfos();
        }

        // Open API 전체 지수 정보를 한 번에 동기화
        try {
            openApiService.syncAndSaveAllIndexInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<SyncJob> syncJobs = indexInfos.stream()
                .map(indexInfo -> saveSyncJob(
                        indexInfo,
                        INDEX_INFO_JOB,
                        null,
                        workerIp,
                        SUCCESS
                ))
                .toList();

        return syncJobs.stream()
                .map(SyncJobDetailResponse::from)
                .toList();
    }

    public List<SyncJobDetailResponse> syncIndexData(
            List<String> indexInfoIds,
            LocalDate baseDateFrom,
            LocalDate baseDateTo,
            String workerIp
    ) {
        validateDateRange(baseDateFrom, baseDateTo);

        List<IndexInfo> targetIndexInfos = findTargetIndexInfos(indexInfoIds);
        List<SyncJobDetailResponse> responses = new ArrayList<>();

        LocalDate targetDate = baseDateFrom;

        while (!targetDate.isAfter(baseDateTo)) {
            try {
                String baseDate = targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);

                // 날짜별 전체 데이터를 한 번에 동기화
                openApiService.syncAndSaveIndexData(
                        null,
                        baseDate,
                        1,
                        100
                );

                for (IndexInfo indexInfo : targetIndexInfos) {
                    SyncJob syncJob = saveSyncJob(
                            indexInfo,
                            INDEX_DATA_JOB,
                            targetDate,
                            workerIp,
                            SUCCESS
                    );
                    responses.add(SyncJobDetailResponse.from(syncJob));
                }

            } catch (Exception e) {
                e.printStackTrace();

                for (IndexInfo indexInfo : targetIndexInfos) {
                    SyncJob syncJob = saveSyncJob(
                            indexInfo,
                            INDEX_DATA_JOB,
                            targetDate,
                            workerIp,
                            FAILED
                    );
                    responses.add(SyncJobDetailResponse.from(syncJob));
                }
            }

            targetDate = targetDate.plusDays(1);
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
        if (indexInfoIds == null
                || indexInfoIds.isEmpty()
                || indexInfoIds.contains("ALL")
                || indexInfoIds.contains("-1")
                || indexInfoIds.contains("")) {
            return findAllIndexInfos();
        }

        return indexInfoIds.stream()
                .filter(id -> id != null && !id.isBlank())
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