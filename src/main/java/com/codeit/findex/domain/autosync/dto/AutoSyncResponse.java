package com.codeit.findex.domain.autosync.dto;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import java.time.LocalDateTime;
import java.util.UUID;

public record AutoSyncResponse(
        UUID indexId,
        Boolean enabled,
        LocalDateTime updatedAt
) {
    public static AutoSyncResponse from(AutoSync autoSync) {
        return new AutoSyncResponse(
                autoSync.getIndexInfo().getId(),
                autoSync.getEnabled(),
                autoSync.getUpdatedAt()
        );
    }
}