package com.codeit.findex.domain.syncjob.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SyncJobDetailResponse (
        UUID id,
        UUID indexId,
        String jobType,
        LocalDate targetDate,
        String worker,
        LocalDateTime jobTime,
        String result
){
}
