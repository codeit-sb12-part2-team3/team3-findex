package com.codeit.findex.domain.dashboard.controller;

import com.codeit.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.codeit.findex.domain.dashboard.service.DashboardFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class DashboardController {

    private final DashboardFavoriteService dashboardFavoriteService;


    @GetMapping("/performance/favorite")
    public ResponseEntity<List<DashboardSummaryResponse>> getFavorites(
            @RequestParam(required = false) String periodType
    ) {
        return ResponseEntity.ok(
                dashboardFavoriteService.getSummary()
        );
    }
}