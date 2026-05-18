package com.codeit.findex.domain.indexinfo.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class IndexInfoResponse {

    private UUID id;

    private String indexName;

    private String indexClassification;

    private Integer employedItemsCount;

    private LocalDate basePointInTime;

    private BigDecimal baseIndex;

    private String sourceType;

    private Boolean favorite;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}