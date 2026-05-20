package com.codeit.findex.domain.indexdata.dto;

import java.time.LocalDate;
import java.util.UUID;

public record IndexDataSearchRequest(
        UUID indexInfoId,
        LocalDate startDate,
        LocalDate endDate,

        String sortField,
        String sortDirection,

        UUID idAfter,
        String cursor,
        Integer size
) {

}
