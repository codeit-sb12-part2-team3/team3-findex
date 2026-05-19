package com.codeit.findex.domain.autosync.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AutoSyncCreateRequest(
        @NotNull(message = "지수 ID는 필수입니다.")
        UUID indexId
) {
}