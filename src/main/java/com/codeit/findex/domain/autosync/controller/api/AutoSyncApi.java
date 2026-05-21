package com.codeit.findex.domain.autosync.controller.api;

import com.codeit.findex.domain.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.domain.autosync.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.domain.autosync.dto.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "자동 연동 설정 API",
        description = "지수 자동 연동 설정 관리 API"
)
public interface AutoSyncApi {

    @Operation(
            summary = "자동 연동 설정 목록 조회",
            description = """
                    자동 연동 설정 목록을 조회합니다.

                    - 지수 ID 필터
                    - 활성화 여부
                    - 정렬
                    - Cursor 페이지네이션 지원
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content=@Content(
                            schema=@Schema(
                                    implementation= ErrorResponse.class
                            )
                    )
            )
    })
    ResponseEntity<CursorPageResponseAutoSyncConfigDto> getAutoSyncConfigs(
            @Parameter(description="다음 페이지 Cursor")
            @RequestParam(required=false) UUID nextIdAfter,

            @Parameter(description="지수 ID")
            @RequestParam(required=false) UUID indexId,

            @Parameter(description="활성 여부")
            @RequestParam(required=false) Boolean enabled,

            @Parameter(description="정렬")
            @RequestParam(required=false) String sort,

            @Parameter(description="조회 개수")
            @RequestParam(defaultValue="10") int size
    );


    @Operation(
            summary = "자동 연동 상태 수정",
            description = """
                    자동 연동 활성 상태를 변경합니다.

                    true : 활성화
                    false : 비활성화
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode="200",description="수정 성공"),
            @ApiResponse(responseCode="404",description="설정 없음")
    })
    ResponseEntity<AutoSyncConfigDto> updateAutoSyncConfig(
            @Parameter(description="설정 ID")
            @PathVariable UUID id,

            @RequestBody
            @Valid AutoSyncConfigUpdateRequest request
    );
}