package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.entity.SourceType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexDataUpdateRequest {
    private SourceType sourceType;

    private Double marketPrice;

    private Double closingPrice;

    private Double highPrice;

    private Double lowPrice;

    private Double versus;

    private Double fluctuationRate;

    private Long tradeQuantity;

    private Long tradePrice;

    private Long marketTotalAmount;
}
