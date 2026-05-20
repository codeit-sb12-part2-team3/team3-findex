package com.codeit.findex.domain.indexdata.dto;


import com.codeit.findex.domain.indexdata.entity.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataDto (
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
            case "marketPrice" -> this.marketPrice!=null ? String.valueOf(this.marketPrice):null;
            case "closingPrice" -> this.marketPrice!=null ? String.valueOf(this.closingPrice):null;
            case "highPrice" -> this.marketPrice!=null ? String.valueOf(this.highPrice):null;
            case "lowPrice" -> this.marketPrice!=null ? String.valueOf(this.lowPrice):null;
            case "versus" -> this.marketPrice!=null ? String.valueOf(this.versus):null;
            case "fluctuationRate" -> this.marketPrice!=null ? String.valueOf(this.fluctuationRate):null;
            case "tradingQuantity" -> this.marketPrice!=null ? String.valueOf(this.tradingQuantity):null;
            case "tradingPrice" -> this.marketPrice!=null ? String.valueOf(this.tradingPrice):null;
            case "marketTotalAmount" -> this.marketPrice!=null ? String.valueOf(this.marketTotalAmount):null;
            default -> this.baseDate != null ? this.baseDate.toString() : null;
        };
    }
}
