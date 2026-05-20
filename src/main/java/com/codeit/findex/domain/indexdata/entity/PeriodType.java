package com.codeit.findex.domain.indexdata.entity;

import java.time.LocalDate;
import java.util.Arrays;

public enum PeriodType {
    DAILY,WEEK,MONTH;

    public static PeriodType fromString(String value){
        if(value == null || value.isBlank()){
            return DAILY;
        }

        return Arrays.stream(PeriodType.values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(DAILY);
    }

    public LocalDate getStartDate(LocalDate endDate) {
        return switch (this){
            case DAILY -> endDate;
            case WEEK -> endDate.minusWeeks(1);
            case  MONTH -> endDate.minusMonths(1);
        };
    }
}
