package com.codeit.findex.domain.indexdata.dto;

import com.codeit.findex.domain.indexdata.entity.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexDataCreateRequest {

    private UUID indexId;

    private Date baseDate;

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
