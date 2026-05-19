package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.entity.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataCreateRequest(
        UUID indexId,

        LocalDate baseDate,

        SourceType sourceType,

        BigDecimal marketPrice,

        BigDecimal closingPrice,

        BigDecimal highPrice,

        BigDecimal lowPrice,

        BigDecimal versus,

        BigDecimal fluctuationRate,

        Long tradeQuantity,

        Long tradePrice,

        Long marketTotalAmount
) {
}
