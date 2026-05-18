package com.codeit.findex.domain.indexinfo.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class IndexInfoCreateRequest {

    private String indexName;

    private String indexClassification;

    private Integer employedItemsCount;

    private LocalDate basePointInTime;

    private BigDecimal baseIndex;

    private String sourceType;

}