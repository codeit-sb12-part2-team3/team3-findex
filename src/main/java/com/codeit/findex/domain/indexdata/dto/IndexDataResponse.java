package com.codeit.findex.domain.indexdata.dto;


import com.codeit.findex.domain.indexdata.entity.SourceType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexDataResponse {

    private UUID id;

    private UUID indexId;

    private LocalDate baseDate;

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
