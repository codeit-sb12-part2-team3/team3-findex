package com.codeit.findex.domain.indexdata.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataCreateRequest(
        UUID indexInfoId,

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
