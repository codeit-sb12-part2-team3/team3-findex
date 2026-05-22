package com.codeit.findex.domain.syncjob.controller.api;

import com.codeit.findex.domain.syncjob.dto.CursorPageResponseSyncJobDto;
import com.codeit.findex.domain.syncjob.dto.SyncJobDetailResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobIndexDataSyncRequest;
import com.codeit.findex.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag(
        name = "동기화 작업 API",
        description = "Open API 연동 및 Sync Job 이력 관리 API"
)
public interface SyncJobApi {

    @Operation(
            summary = "동기화 이력 조회",
            description = """
                    동기화 작업 이력을 조회합니다.
                    
                    지원 기능:
                    - 작업 유형(jobType)
                    - 상태(status)
                    - 작업자(worker)
                    - 날짜 범위
                    - Cursor 기반 페이지네이션
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "동기화 이력 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            schema=@Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    ResponseEntity<CursorPageResponseSyncJobDto> getSyncJobList(

            @Parameter(description="작업 유형")
            @RequestParam(required = false)
            String jobType,

            @Parameter(description="지수 ID (UUID 또는 numeric ID)")
            @RequestParam(name="indexInfoId",required=false)
            String indexInfoId,

            @Parameter(description="대상 날짜")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required=false)
            LocalDate targetDate,

            @Parameter(description="작업자")
            @RequestParam(required=false)
            String worker,

            @Parameter(description="성공/실패 상태")
            @RequestParam(required=false)
            String status,

            @Parameter(description="작업 시작 시간")
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required=false)
            LocalDateTime jobTimeFrom,

            @Parameter(description="작업 종료 시간")
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required=false)
            LocalDateTime jobTimeTo,

            @Parameter(description="Cursor 시간")
            @RequestParam(required=false)
            LocalDateTime lastJobTime,

            @Parameter(description="Cursor ID")
            @RequestParam(required=false)
            UUID lastId,

            @Parameter(description="정렬 기준")
            @RequestParam(defaultValue="jobTime")
            String sortField,

            @Parameter(description="정렬 방향")
            @RequestParam(defaultValue="desc")
            String sortDirection,

            @Parameter(description="조회 개수")
            @RequestParam(defaultValue="10")
            int size
    );


    @Operation(
            summary = "지수 정보 Open API 동기화",
            description = """
                    Open API를 통해 전체 지수 정보를 동기화합니다.
                    
                    신규 데이터 저장 및 기존 데이터 업데이트를 수행합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="동기화 성공",
                    content=@Content(
                            array=@ArraySchema(
                                    schema=@Schema(
                                            implementation=SyncJobDetailResponse.class
                                    )
                            )
                    )
            )
    })
    List<SyncJobDetailResponse> syncIndexInfo(
            HttpServletRequest request
    );


    @Operation(
            summary = "지수 데이터 Open API 동기화",
            description = """
                    선택한 지수 데이터를 Open API와 동기화합니다.

                    - 전체 지수 선택 가능
                    - 날짜 범위 지정 가능
                    - Sync Job 기록 저장
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="동기화 성공"
            ),
            @ApiResponse(
                    responseCode="400",
                    description="잘못된 날짜 범위"
            )
    })
    List<SyncJobDetailResponse> syncIndexData(

            @RequestBody
            SyncJobIndexDataSyncRequest requestDto,

            HttpServletRequest request
    );
}