package com.codeit.findex.domain.indexdata.dto;

import java.util.List;

public record IndexDataSearchResponse<T>(
        List<T> content,
        String nextCursor ,
        String nextIdAfter,
        Integer size,
        Integer totalElements,
        Boolean hasNext
) {
}

