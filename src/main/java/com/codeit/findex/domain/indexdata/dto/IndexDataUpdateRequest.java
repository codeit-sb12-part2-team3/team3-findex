package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.entity.SourceType;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexDataUpdateRequest {
    private SourceType sourceType;

    private BigDecimal marketPrice;

    private BigDecimal closingPrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private BigDecimal versus;

    private BigDecimal fluctuationRate;

    private Long tradeQuantity;

    private Long tradePrice;

    private Long marketTotalAmount;
}
