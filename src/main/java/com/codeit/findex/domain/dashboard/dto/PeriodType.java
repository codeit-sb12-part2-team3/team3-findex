package com.codeit.findex.domain.dashboard.dto;

import java.time.LocalDate;

public enum PeriodType {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

    public LocalDate getStartDate(LocalDate endDate) {
        return switch (this) {
            case DAILY -> endDate.minusDays(1);
            case WEEKLY -> endDate.minusWeeks(1);
            case MONTHLY -> endDate.minusMonths(1);
            case QUARTERLY -> endDate.minusMonths(3);
            case YEARLY -> endDate.minusYears(1);
        };
    }
}