package com.codeit.findex.domain.dashboard.controller.api;

import com.codeit.findex.domain.dashboard.dto.IndexChartDto;
import com.codeit.findex.domain.dashboard.dto.IndexPerformanceDto;
import com.codeit.findex.domain.dashboard.dto.PeriodType;
import com.codeit.findex.domain.dashboard.dto.RankedIndexPerformanceDto;
import com.codeit.findex.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "대시보드 API",
        description = "대시보드 지수 성과 및 차트 조회 API"
)
public interface DashboardApi {

    @Operation(
            summary = "즐겨찾기 지수 성과 조회",
            description = """
                    즐겨찾기로 등록한 지수의 성과 정보를 조회합니다.

                    지원 기간:
                    - DAILY
                    - WEEKLY
                    - MONTHLY
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성과 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = IndexPerformanceDto.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            schema=@Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    ResponseEntity<List<IndexPerformanceDto>> getFavoritePerformance(

            @Parameter(description = "조회 기간")
            @RequestParam(defaultValue = "DAILY")
            PeriodType periodType
    );


    @Operation(
            summary = "지수 성과 순위 조회",
            description = """
                    기간별 지수 성과 순위를 조회합니다.

                    지원 기간:
                    - DAILY
                    - WEEKLY
                    - MONTHLY
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="순위 조회 성공"
            )
    })
    ResponseEntity<List<RankedIndexPerformanceDto>> getPerformanceRank(

            @Parameter(description="조회 기간")
            @RequestParam(defaultValue="DAILY")
            PeriodType periodType,

            @Parameter(description="지수 ID (선택)")
            @RequestParam(required = false)
            UUID indexInfoId,

            @Parameter(description="조회 개수 (기본 10)")
            @RequestParam(defaultValue = "10")
            int limit
    );


    @Operation(
            summary = "지수 차트 조회",
            description = """
                    특정 지수의 차트 데이터를 조회합니다.

                    지원 기간:
                    - DAILY
                    - WEEKLY
                    - MONTHLY
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="차트 조회 성공"
            ),
            @ApiResponse(
                    responseCode="404",
                    description="존재하지 않는 지수"
            )
    })
    ResponseEntity<IndexChartDto> getIndexChart(

            @Parameter(description = "지수 ID")
            @PathVariable UUID id,

            @Parameter(description = "조회 기간")
            @RequestParam(defaultValue = "MONTHLY")
            PeriodType periodType
    );
}