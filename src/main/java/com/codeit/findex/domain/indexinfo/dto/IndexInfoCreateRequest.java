package com.codeit.findex.domain.indexinfo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoCreateRequest(

        String indexName,
        String indexClassification,
        Integer employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        //String sourceType,
        Boolean favorite
) {
}



