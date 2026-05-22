package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.syncjob.entity.SyncJob;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SyncJobDetailResponse(
        UUID id,
        int indexInfoId,
        String indexName,
        String jobType,
        LocalDate targetDate,
        String worker,
        LocalDateTime jobTime,
        String result
){
    public static SyncJobDetailResponse from(SyncJob syncJob) {
        UUID uuid = syncJob.getIndexInfo().getId();
        int numericId = uuid.hashCode() == 0 ? 1 : uuid.hashCode();
        return new SyncJobDetailResponse(
                syncJob.getId(),
                numericId,
                syncJob.getIndexInfo().getIndexName(),
                syncJob.getJobType(),
                syncJob.getTargetDate(),
                syncJob.getWorker(),
                syncJob.getJobTime(),
                syncJob.getResult()
        );
    }
}