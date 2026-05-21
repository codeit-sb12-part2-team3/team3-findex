package com.codeit.findex.domain.dashboard.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record IndexPerformanceDto(
        UUID indexInfoId,
        String indexClassification,
        String indexName,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        BigDecimal currentPrice,
        BigDecimal beforePrice
) {
}