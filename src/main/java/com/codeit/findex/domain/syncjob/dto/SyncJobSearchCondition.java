package com.codeit.findex.domain.syncjob.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SyncJobSearchCondition(
        String jobType,
        UUID indexId,
        LocalDate targetDate,
        String worker,
        String result,

        LocalDateTime jobTimeFrom,
        LocalDateTime jobTimeTo
) {
}
