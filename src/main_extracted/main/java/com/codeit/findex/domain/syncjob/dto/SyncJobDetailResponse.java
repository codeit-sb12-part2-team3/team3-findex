package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.syncjob.entity.SyncJob;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SyncJobDetailResponse(
        UUID id,
        UUID indexInfoId,
        String indexName,
        String jobType,
        LocalDate targetDate,
        String worker,
        LocalDateTime jobTime,
        String result
){
    public static SyncJobDetailResponse from(SyncJob syncJob) {
        return new SyncJobDetailResponse(
                syncJob.getId(),
                syncJob.getIndexInfo().getId(),
                syncJob.getIndexInfo().getIndexName(),
                syncJob.getJobType(),
                syncJob.getTargetDate(),
                syncJob.getWorker(),
                syncJob.getJobTime(),
                syncJob.getResult()
        );
    }
}