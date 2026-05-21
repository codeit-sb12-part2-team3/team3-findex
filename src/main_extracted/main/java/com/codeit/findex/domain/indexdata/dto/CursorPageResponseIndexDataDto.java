package com.codeit.findex.domain.indexdata.dto;

import java.util.List;
import java.util.UUID;

public record CursorPageResponseIndexDataDto<T>(
        List<T> content,
        String nextCursor ,
        UUID nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}

