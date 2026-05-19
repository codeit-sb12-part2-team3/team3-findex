package com.codeit.findex.domain.syncjob.controller;

import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class SyncJobController {

    private final SyncJobService syncJobService;

    @GetMapping
    public Slice<SyncJobListResponse> getSyncJobList(
            @RequestParam(required = false)
            String jobType,

            @RequestParam(required = false)
            UUID indexId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate targetDate,

            @RequestParam(required = false)
            String worker,

            @RequestParam(required = false)
            String result,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime jobTimeFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime jobTimeTo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime lastJobTime,

            @RequestParam(defaultValue = "10")
            int size

    ) {

        SyncJobSearchCondition condition = new SyncJobSearchCondition(
                jobType,
                indexId,
                targetDate,
                worker,
                result,
                jobTimeFrom,
                jobTimeTo
        );

        return syncJobService.getSyncJobList(
                condition,
                lastJobTime,
                size
        );

    }

}
