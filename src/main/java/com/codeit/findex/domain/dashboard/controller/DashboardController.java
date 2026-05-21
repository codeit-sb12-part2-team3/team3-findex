package com.codeit.findex.domain.dashboard.controller;

import com.codeit.findex.domain.dashboard.dto.IndexInfoSummaryDto;
import com.codeit.findex.domain.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /*
     * 대시보드 지수 정보 요약 목록 조회
     * [GET] /api/dashboard/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<List<IndexInfoSummaryDto>> getDashboardSummary() {
        List<IndexInfoSummaryDto> response = dashboardService.getDashboardSummaries();
        return ResponseEntity.ok(response);
    }
}