package com.codeit.findex.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IndexPerformanceResponse {

    private int rank;
    private DashboardSummaryResponse performance;
}