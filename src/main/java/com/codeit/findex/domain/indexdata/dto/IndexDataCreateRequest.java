package com.codeit.findex.domain.indexdata.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataCreateRequest(
        String indexInfoId,

        LocalDate baseDate,

        BigDecimal marketPrice,

        BigDecimal closingPrice,

        BigDecimal highPrice,

        BigDecimal lowPrice,

        BigDecimal versus,

        BigDecimal fluctuationRate,

        Long tradingQuantity,

        Long tradingPrice,

        Long marketTotalAmount
) {
}
