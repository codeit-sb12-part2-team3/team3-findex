package com.codeit.findex.domain.indexdata.dto;


import com.codeit.findex.domain.indexdata.entity.SourceType;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexDataResponse {

    private UUID id;

    private UUID index;

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
