package com.codeit.findex.domain.indexdata.dto;

import java.util.List;
import java.util.UUID;

public record IndexChartDto(
        UUID indexInfoId,
        String indexClassification,
        String indexName,
        String periodType,
        List<ChartDataPoint> dataPoints,
        List<ChartDataPoint> ma5DataPoints,
        List<ChartDataPoint> ma20DataPoints
) {
    public record ChartDataPoint(String date, double value) {}
}
