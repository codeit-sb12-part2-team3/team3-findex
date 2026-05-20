package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class SyncJobController {

    private final SyncJobService syncJobService;

    @GetMapping
    public Slice<SyncJobListResponse> getSyncJobList(
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) UUID indexId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) String result,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime jobTimeFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime jobTimeTo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastJobTime,
            @RequestParam(required = false) UUID lastId,
            @RequestParam(defaultValue = "10") int size
    ) {
        SyncJobSearchCondition condition = new SyncJobSearchCondition(
                jobType, indexId, targetDate, worker, result, jobTimeFrom, jobTimeTo
        );
        return syncJobService.getSyncJobList(condition, lastJobTime, lastId, size);
    }

    @PostMapping("/index-infos")
    public List<SyncJobListResponse> syncIndexInfo(
            HttpServletRequest request
    ) {
        String workerIp = request.getRemoteAddr();
        return syncJobService.syncIndexInfo(workerIp);
    }

    @PostMapping("/index-data")
    public List<SyncJobListResponse> syncIndexData(
            @RequestParam(required = false) UUID indexId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request
    ) {
        String workerIp = request.getRemoteAddr();
        return syncJobService.syncIndexData(indexId, startDate, endDate, workerIp);
    }
}