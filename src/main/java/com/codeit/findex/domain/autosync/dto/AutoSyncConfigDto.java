package com.codeit.findex.domain.autosync.dto;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AutoSyncConfigDto(
        UUID id,
        UUID indexInfoId,
        String indexClassification,
        String indexName,
        Boolean enabled
) {
        public static AutoSyncConfigDto from(AutoSync autoSync) {
                return AutoSyncConfigDto.builder()
                        .id(autoSync.getId())
                        .indexInfoId(autoSync.getIndexInfo().getId())
                        .indexClassification(autoSync.getIndexInfo().getIndexClassification())
                        .indexName(autoSync.getIndexInfo().getIndexName())
                        .enabled(autoSync.getEnabled())
                        .build();
        }
}