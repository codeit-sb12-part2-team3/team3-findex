package com.codeit.findex.domain.indexinfo.controller.api;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.global.common.dto.CursorPageResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "지수 정보 API",
        description = "지수 정보 등록, 수정, 삭제, 조회 API"
)
public interface IndexInfoApi {

    @Operation(
            summary = "지수 정보 등록",
            description = "사용자가 새로운 지수 정보를 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 등록 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema=@Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<IndexInfoResponse> create(
            @RequestBody IndexInfoCreateRequest request
    );


    @Operation(
            summary = "지수 정보 목록 조회",
            description = """
                    지수 목록을 조회합니다.

                    지원 기능:
                    - 지수 분류 검색
                    - 지수 이름 검색
                    - 즐겨찾기 필터
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode="200",description="목록 조회 성공")
    })
    ResponseEntity<CursorPageResponse<IndexInfoResponse>> findAll(

            @Parameter(description="지수 분류")
            @RequestParam(required=false)
            String indexClassification,

            @Parameter(description="지수 이름")
            @RequestParam(required=false)
            String indexName,

            @Parameter(description="즐겨찾기 여부")
            @RequestParam(required=false)
            Boolean favorite,

            @Parameter(description="정렬 필드 (indexClassification, indexName, employedItemsCount)")
            @RequestParam(required=false, defaultValue="indexClassification")
            String sortField,

            @Parameter(description="정렬 방향 (asc, desc)")
            @RequestParam(required=false, defaultValue="asc")
            String sortDirection
    );


    @Operation(
            summary = "지수 요약 목록 조회",
            description = "지수 목록을 간단한 형태로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="조회 성공",
                    content=@Content(
                            array=@ArraySchema(
                                    schema=@Schema(
                                            implementation=IndexInfoResponse.class
                                    )
                            )
                    )
            )
    })
    ResponseEntity<List<IndexInfoSummaryDto>> getSummaries();


    @Operation(
            summary="지수 단건 조회",
            description="ID로 특정 지수를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode="200",description="조회 성공"),
            @ApiResponse(responseCode="404",description="존재하지 않는 지수")
    })
    ResponseEntity<IndexInfoResponse> findById(

            @Parameter(description="지수 ID")
            @PathVariable UUID id
    );


    @Operation(
            summary="지수 정보 수정",
            description="지수 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode="200",description="수정 성공"),
            @ApiResponse(responseCode="404",description="존재하지 않는 지수")
    })
    ResponseEntity<IndexInfoResponse> update(

            @Parameter(description="지수 ID")
            @PathVariable UUID id,

            @RequestBody
            IndexInfoUpdateRequest request
    );


    @Operation(
            summary="지수 정보 삭제",
            description="지수 정보를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode="204",description="삭제 성공"),
            @ApiResponse(responseCode="404",description="존재하지 않는 지수")
    })
    ResponseEntity<Void> delete(

            @Parameter(description="지수 ID")
            @PathVariable UUID id
    );
}