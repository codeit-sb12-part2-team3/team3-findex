package com.codeit.findex.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class DashboardSummaryResponse {

    private UUID indexInfoId;
    private String indexName;
    private String indexClassification;

    private LocalDate baseDate;

    private BigDecimal closingPrice;

    private BigDecimal versus;

    private BigDecimal fluctuationRate;

    private BigDecimal currentPrice;
    private BigDecimal beforePrice;
}