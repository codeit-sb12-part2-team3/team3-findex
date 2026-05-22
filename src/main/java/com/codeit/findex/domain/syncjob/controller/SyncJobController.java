package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.controller.api.SyncJobApi;
import com.codeit.findex.domain.syncjob.dto.CursorPageResponseSyncJobDto;
import com.codeit.findex.domain.syncjob.dto.SyncJobIndexDataSyncRequest;
import com.codeit.findex.domain.syncjob.dto.SyncJobDetailResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import com.codeit.findex.global.util.UuidResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class SyncJobController implements SyncJobApi {

    private final SyncJobService syncJobService;
    private final UuidResolver uuidResolver;

    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJobDto> getSyncJobList(
            @RequestParam(required = false) String jobType,
            @RequestParam(name = "indexInfoId", required = false) String indexInfoId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime jobTimeFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime jobTimeTo,
            @RequestParam(name = "cursor", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastJobTime,
            @RequestParam(name = "idAfter", required = false) UUID lastId,
            @RequestParam(required = false, defaultValue = "jobTime") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID indexId = uuidResolver.resolve(indexInfoId);
        SyncJobSearchCondition condition = new SyncJobSearchCondition(
                jobType, indexId, targetDate, worker, status, jobTimeFrom, jobTimeTo
        );

        CursorPageResponseSyncJobDto response = syncJobService.getSyncJobList(
                condition, lastJobTime, lastId, sortField, sortDirection, size
        );

        return ResponseEntity.ok(response);
    }

    private String normalizeIp(String remoteAddr) {
        if ("::1".equals(remoteAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            return "127.0.0.1";
        }
        return remoteAddr;
    }

    @PostMapping("/index-infos")
    public List<SyncJobDetailResponse> syncIndexInfo(
            HttpServletRequest request
    ) {
        String workerIp = normalizeIp(request.getRemoteAddr());
        return syncJobService.syncIndexInfo(workerIp);
    }

    @PostMapping("/index-data")
    public List<SyncJobDetailResponse> syncIndexData(
            @RequestBody SyncJobIndexDataSyncRequest requestDto,
            HttpServletRequest request
    ) {
        String workerIp = normalizeIp(request.getRemoteAddr());
        return syncJobService.syncIndexData(
                requestDto.indexInfoIds(),
                requestDto.baseDateFrom(),
                requestDto.baseDateTo(),
                workerIp
        );
    }
}