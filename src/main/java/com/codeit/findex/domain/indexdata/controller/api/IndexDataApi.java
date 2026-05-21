package com.codeit.findex.domain.indexdata.controller.api;

import com.codeit.findex.domain.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataDto;
import com.codeit.findex.domain.indexdata.dto.IndexDataSearchRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

@Tag(
        name = "지수 데이터 API",
        description = "지수 데이터 등록, 수정, 삭제, 목록 조회 및 CSV 내보내기 API"
)
public interface IndexDataApi {

    @Operation(
            summary = "지수 데이터 등록",
            description = """
                    사용자가 직접 지수 데이터를 등록합니다.
                    
                    등록 시 입력한 지수 정보 ID와 기준일자를 기준으로 데이터가 저장됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "지수 데이터 등록 성공",
                    content = @Content(schema = @Schema(implementation = IndexDataDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "지수 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<IndexDataDto> create(
            @RequestBody IndexDataCreateRequest request
    );

    @Operation(
            summary = "지수 데이터 수정",
            description = """
                    기존 지수 데이터를 수정합니다.
                    
                    요청 body에 포함된 값만 수정 대상이 됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지수 데이터 수정 성공",
                    content = @Content(schema = @Schema(implementation = IndexDataDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 지수 데이터를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<IndexDataDto> update(
            @Parameter(description = "지수 데이터 ID")
            @PathVariable UUID id,

            @RequestBody IndexDataUpdateRequest request
    );

    @Operation(
            summary = "지수 데이터 삭제",
            description = "지수 데이터 ID를 기준으로 데이터를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "지수 데이터 삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 지수 데이터를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "지수 데이터 ID")
            @PathVariable UUID id
    );

    @Operation(
            summary = "지수 데이터 목록 조회",
            description = """
                    지수 데이터 목록을 조회합니다.
                    
                    지원 기능:
                    - 지수 정보 ID 필터링
                    - 날짜 범위 필터링
                    - 정렬
                    - Cursor 기반 페이지네이션
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지수 데이터 목록 조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 검색 조건",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<CursorPageResponseIndexDataDto<IndexDataDto>> searchIndexData(
            @ParameterObject
            @ModelAttribute
            IndexDataSearchRequest searchRequest
    );

    @Operation(
            summary = "지수 데이터 CSV 다운로드",
            description = """
                    검색 조건에 해당하는 지수 데이터를 CSV 파일로 다운로드합니다.
                    
                    UTF-8 BOM을 포함하여 Excel에서 한글이 깨지지 않도록 처리합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV 다운로드 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "CSV 생성 중 서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<StreamingResponseBody> exportCsv(
            @ParameterObject
            @ModelAttribute
            IndexDataSearchRequest searchRequest
    );
}