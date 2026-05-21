package com.codeit.findex.domain.dashboard.dto;

import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
public record IndexChartDto(
        UUID indexInfoId,
        String indexClassification,
        String indexName,
        String periodType,
        List<ChartDataPoint> dataPoints,   // 실제 지수 종가 차트
        List<ChartDataPoint> ma5DataPoints, // 5일 이동평균선 차트
        List<ChartDataPoint> ma20DataPoints // 20일 이동평균선 차트
) {
}