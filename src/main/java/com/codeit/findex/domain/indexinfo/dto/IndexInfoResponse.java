package com.codeit.findex.domain.indexinfo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record IndexInfoResponse(

        UUID id,
        String indexName,
        String indexClassification,
        Integer employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        String sourceType,
        Boolean favorite,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}