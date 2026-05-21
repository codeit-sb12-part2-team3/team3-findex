package com.codeit.findex.domain.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "순위가 포함된 지수 성과 정보 DTO")
public record RankedIndexPerformanceDto(

        @Schema(description = "지수 성과 정보")
        IndexPerformanceDto performance,

        @Schema(description = "순위 (1등, 2등...)")
        Integer rank
) {
}