package com.codeit.findex.domain.dashboard.controller;

import com.codeit.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.codeit.findex.domain.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<List<DashboardSummaryResponse>> getFavoriteIndexSummaries() {

        List<DashboardSummaryResponse> responses = dashboardService.getFavoriteIndexSummaries();

        return ResponseEntity.ok(responses);
    }
}