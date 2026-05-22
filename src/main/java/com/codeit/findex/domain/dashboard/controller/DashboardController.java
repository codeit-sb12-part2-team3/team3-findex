package com.codeit.findex.domain.dashboard.controller;

import com.codeit.findex.domain.dashboard.controller.api.DashboardApi;
import com.codeit.findex.domain.dashboard.dto.IndexChartDto;
import com.codeit.findex.domain.dashboard.dto.IndexPerformanceDto;
import com.codeit.findex.domain.dashboard.dto.PeriodType;
import com.codeit.findex.domain.dashboard.dto.RankedIndexPerformanceDto;
import com.codeit.findex.domain.dashboard.service.DashboardService;
import com.codeit.findex.global.util.UuidResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final DashboardService dashboardService;
    private final UuidResolver uuidResolver;

    @GetMapping("/performance/favorite")
    public ResponseEntity<List<IndexPerformanceDto>> getFavoritePerformance(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        return ResponseEntity.ok(dashboardService.getFavoritePerformance(periodType));
    }

    @GetMapping("/performance/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> getPerformanceRank(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType,
            @RequestParam(required = false) String indexInfoId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        UUID uuid = resolveUuid(indexInfoId);
        return ResponseEntity.ok(dashboardService.getRankedPerformance(periodType, uuid, limit));
    }

    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartDto> getIndexChart(
            @PathVariable String id,
            @RequestParam(defaultValue = "MONTHLY") PeriodType periodType
    ) {
        UUID uuid = resolveUuid(id);
        if (uuid == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(dashboardService.getIndexChart(uuid, periodType));
    }

    private UUID resolveUuid(String id) {
        return uuidResolver.resolve(id);
    }
}