package com.codeit.findex.domain.syncjob.dto;

import java.util.List;
import java.util.UUID;

public record CursorPageResponseSyncJobDto(
        List<SyncJobDetailResponse> content,
        String nextCursor,
        UUID nextIdAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}