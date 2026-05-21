package com.codeit.findex.domain.syncjob.dto;

import com.codeit.findex.domain.syncjob.entity.SyncJob;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SyncJobListResponse (
        UUID id,
        UUID indexInfoId,
        String jobType,
        LocalDate targetDate,
        String worker,
        LocalDateTime jobTime,
        String result
){
    public static SyncJobListResponse from(SyncJob syncJob) {

        return new SyncJobListResponse(
                syncJob.getId(),
                syncJob.getIndexInfo().getId(),
                syncJob.getJobType(),
                syncJob.getTargetDate(),
                syncJob.getWorker(),
                syncJob.getJobTime(),
                syncJob.getResult()
        );
    }

}
