package com.codeit.findex.domain.autosync.dto;

import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
public record CursorPageResponseAutoSyncConfigDto(
        List<AutoSyncConfigDto> content, // 데이터 목록
        String nextCursor,               // 다음 커서 (보통 정렬 기준값 + ID를 합친 문자열)
        UUID nextIdAfter,                // 이전 페이지의 마지막 요소 ID
        Integer size,                    // 페이지에 포함된 데이터 개수
        Long totalElements,              // 전체 데이터 개수
        Boolean hasNext                  // 다음 페이지 존재 여부
) {
    public static CursorPageResponseAutoSyncConfigDto of(
            List<AutoSyncConfigDto> content,
            String nextCursor,
            UUID nextIdAfter,
            Integer size,
            Long totalElements,
            Boolean hasNext) {

        return CursorPageResponseAutoSyncConfigDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .size(size)
                .totalElements(totalElements)
                .hasNext(hasNext)
                .build();
    }
}