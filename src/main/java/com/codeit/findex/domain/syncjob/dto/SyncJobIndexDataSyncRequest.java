package com.codeit.findex.domain.syncjob.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public record SyncJobIndexDataSyncRequest(
        List<String> indexInfoIds,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate baseDateFrom,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate baseDateTo
) {
}