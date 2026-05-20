package com.codeit.findex.domain.indexdata.dto;

import java.math.BigDecimal;
import java.util.UUID;

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