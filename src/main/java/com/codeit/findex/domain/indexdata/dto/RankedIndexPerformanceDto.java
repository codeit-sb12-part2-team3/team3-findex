package com.codeit.findex.domain.indexdata.dto;

public record RankedIndexPerformanceDto(
        IndexPerformanceDto indexPerformanceDto,
        int rank
) {

}
