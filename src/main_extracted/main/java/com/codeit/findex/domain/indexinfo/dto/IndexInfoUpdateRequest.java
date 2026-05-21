package com.codeit.findex.domain.indexinfo.dto;

import java.math.BigDecimal;

import java.time.LocalDate;

public record IndexInfoUpdateRequest(
        Integer employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        Boolean favorite
) {

}