package com.codeit.findex.domain.dashboard.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record IndexInfoSummaryDto(
        UUID id,
        String indexClassification,
        String indexName
) {
}