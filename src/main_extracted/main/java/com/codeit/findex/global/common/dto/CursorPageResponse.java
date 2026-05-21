package com.codeit.findex.global.common.dto;

import java.util.List;
import java.util.UUID;

public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        UUID nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}