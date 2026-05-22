package com.codeit.findex.domain.dashboard.controller;

import com.codeit.findex.domain.dashboard.controller.api.DashboardApi;
import com.codeit.findex.domain.dashboard.dto.IndexChartDto;
import com.codeit.findex.domain.dashboard.dto.IndexPerformanceDto;
import com.codeit.findex.domain.dashboard.dto.PeriodType;
import com.codeit.findex.domain.dashboard.dto.RankedIndexPerformanceDto;
import com.codeit.findex.domain.dashboard.service.DashboardService;
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

    @GetMapping("/performance/favorite")
    public ResponseEntity<List<IndexPerformanceDto>> getFavoritePerformance(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        return ResponseEntity.ok(dashboardService.getFavoritePerformance(periodType));
    }

    @GetMapping("/performance/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> getPerformanceRank(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType,
            @RequestParam(required = false) UUID indexInfoId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(dashboardService.getRankedPerformance(periodType, indexInfoId, limit));
    }

    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartDto> getIndexChart(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "MONTHLY") PeriodType periodType
    ) {
        return ResponseEntity.ok(dashboardService.getIndexChart(id, periodType));
    }
}