package com.codeit.findex.domain.indexdata.dto;


import com.codeit.findex.domain.indexdata.entity.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataResponse (
        UUID id,
        UUID indexInfoId,
        LocalDate baseDate,
        SourceType sourceType,
        BigDecimal marketPrice,
        BigDecimal closingPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        Long tradingQuantity,
        Long tradingPrice,
        Long marketTotalAmount
){
    public String getCursorValueByField(String sortField) {

        if (sortField == null) {
            return this.baseDate != null ? this.baseDate.toString() : null;
        }

        return switch (sortField) {
            case "marketPrice" -> String.valueOf(this.marketPrice);
            case "closingPrice" -> String.valueOf(this.closingPrice);
            case "highPrice" -> String.valueOf(this.highPrice);
            case "lowPrice" -> String.valueOf(this.lowPrice);
            case "versus" -> String.valueOf(this.versus);
            case "fluctuationRate" -> String.valueOf(this.fluctuationRate);
            case "tradingQuantity" -> String.valueOf(this.tradingQuantity);
            case "tradingPrice" -> String.valueOf(this.tradingPrice);
            case "marketTotalAmount" -> String.valueOf(this.marketTotalAmount);
            default -> this.baseDate != null ? this.baseDate.toString() : null;
        };
    }
}
