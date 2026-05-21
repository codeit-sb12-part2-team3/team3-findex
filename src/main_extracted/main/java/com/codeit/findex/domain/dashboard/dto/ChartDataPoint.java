package com.codeit.findex.domain.dashboard.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ChartDataPoint(
        LocalDate date,
        BigDecimal value
) {
}