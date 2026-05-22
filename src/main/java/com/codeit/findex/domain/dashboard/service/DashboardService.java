package com.codeit.findex.domain.dashboard.service;

import com.codeit.findex.domain.dashboard.dto.*;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;

    // 즐겨찾기 요약
    public List<IndexPerformanceDto> getFavoritePerformance(PeriodType periodType) {
        List<IndexInfo> favorites = indexInfoRepository.findByFavoriteTrue();
        if (favorites.isEmpty()) return List.of();

        if (periodType == PeriodType.DAILY) {
            List<IndexData> favoriteData = indexDataRepository.findLatestFavoriteIndexData();
            Map<UUID, IndexData> dataByInfoId = favoriteData.stream()
                    .collect(Collectors.toMap(d -> d.getIndexInfo().getId(), d -> d));

            return favorites.stream()
                    .map(info -> {
                        IndexData data = dataByInfoId.get(info.getId());
                        if (data != null) return mapToPerformanceDto(data);
                        return zeroPerformance(info);
                    })
                    .collect(Collectors.toList());
        }

        // 기간 수익률 계산 (주간/월간/분기/연간)
        LocalDate endDate = indexDataRepository.findGlobalMaxBaseDate();
        if (endDate == null) return favorites.stream().map(this::zeroPerformance).collect(Collectors.toList());
        LocalDate startDate = periodType.getStartDate(endDate);

        Set<UUID> favoriteIds = favorites.stream().map(IndexInfo::getId).collect(Collectors.toSet());

        Map<UUID, IndexData> latestMap = indexDataRepository.findLatestDataPerIndexInPeriod(startDate, endDate, null)
                .stream()
                .filter(d -> favoriteIds.contains(d.getIndexInfo().getId()))
                .collect(Collectors.toMap(d -> d.getIndexInfo().getId(), d -> d));
        Map<UUID, IndexData> earliestMap = indexDataRepository.findEarliestDataPerIndexInPeriod(startDate, endDate, null)
                .stream()
                .filter(d -> favoriteIds.contains(d.getIndexInfo().getId()))
                .collect(Collectors.toMap(d -> d.getIndexInfo().getId(), d -> d));

        return favorites.stream()
                .map(info -> {
                    IndexData latest = latestMap.get(info.getId());
                    IndexData earliest = earliestMap.get(info.getId());
                    if (latest != null && earliest != null) return mapToPeriodPerformanceDto(latest, earliest);
                    return zeroPerformance(info);
                })
                .collect(Collectors.toList());
    }

    private IndexPerformanceDto zeroPerformance(IndexInfo info) {
        return IndexPerformanceDto.builder()
                .indexInfoId(info.getId())
                .indexClassification(info.getIndexClassification())
                .indexName(info.getIndexName())
                .versus(BigDecimal.ZERO)
                .fluctuationRate(BigDecimal.ZERO)
                .currentPrice(BigDecimal.ZERO)
                .beforePrice(BigDecimal.ZERO)
                .build();
    }

    // 성과 랭킹
    public List<RankedIndexPerformanceDto> getRankedPerformance(PeriodType periodType, UUID indexInfoId, int limit) {
        LocalDate endDate = indexDataRepository.findGlobalMaxBaseDate();
        if (endDate == null) return List.of();
        LocalDate startDate = periodType.getStartDate(endDate);

        if (periodType == PeriodType.DAILY) {
            List<IndexData> topData = indexDataRepository.findTopRankedIndexData(startDate, endDate, indexInfoId, PageRequest.of(0, limit));
            return IntStream.range(0, topData.size())
                    .mapToObj(i -> RankedIndexPerformanceDto.builder()
                            .rank(i + 1)
                            .performance(mapToPerformanceDto(topData.get(i)))
                            .build())
                    .collect(Collectors.toList());
        }

        List<IndexData> latestList = indexDataRepository.findLatestDataPerIndexInPeriod(startDate, endDate, indexInfoId);
        List<IndexData> earliestList = indexDataRepository.findEarliestDataPerIndexInPeriod(startDate, endDate, indexInfoId);

        Map<UUID, IndexData> earliestMap = earliestList.stream()
                .collect(Collectors.toMap(d -> d.getIndexInfo().getId(), d -> d));

        List<IndexPerformanceDto> performances = latestList.stream()
                .filter(latest -> earliestMap.containsKey(latest.getIndexInfo().getId()))
                .map(latest -> mapToPeriodPerformanceDto(latest, earliestMap.get(latest.getIndexInfo().getId())))
                .sorted(Comparator.comparing(IndexPerformanceDto::fluctuationRate).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return IntStream.range(0, performances.size())
                .mapToObj(i -> RankedIndexPerformanceDto.builder()
                        .rank(i + 1)
                        .performance(performances.get(i))
                        .build())
                .collect(Collectors.toList());
    }

    // 차트 & 이동평균선
    public IndexChartDto getIndexChart(UUID indexInfoId, PeriodType periodType) {
        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
                .orElseThrow(() -> new IllegalArgumentException("지수 정보가 없습니다."));

        LocalDate latestDate = indexDataRepository.findMaxBaseDateByIndexInfoId(indexInfoId);
        LocalDate endDate = latestDate != null ? latestDate : LocalDate.now();
        LocalDate startDate = periodType.getStartDate(endDate);

        List<IndexData> dataList = indexDataRepository.findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(indexInfoId, startDate, endDate);

        List<ChartDataPoint> dataPoints = dataList.stream()
                .map(d -> new ChartDataPoint(d.getBaseDate(), d.getClosingPrice()))
                .collect(Collectors.toList());

        List<ChartDataPoint> ma5 = calculateMovingAverage(dataPoints, 5);
        List<ChartDataPoint> ma20 = calculateMovingAverage(dataPoints, 20);

        Collections.reverse(dataPoints);

        return IndexChartDto.builder()
                .indexInfoId(indexInfoId)
                .indexClassification(indexInfo.getIndexClassification())
                .indexName(indexInfo.getIndexName())
                .periodType(periodType.name())
                .dataPoints(dataPoints)
                .ma5DataPoints(ma5)
                .ma20DataPoints(ma20)
                .build();
    }

    // 매핑
    private IndexPerformanceDto mapToPerformanceDto(IndexData data) {
        return IndexPerformanceDto.builder()
                .indexInfoId(data.getIndexInfo().getId())
                .indexClassification(data.getIndexInfo().getIndexClassification())
                .indexName(data.getIndexInfo().getIndexName())
                .versus(data.getVersus())
                .fluctuationRate(data.getFluctuationRate())
                .currentPrice(data.getClosingPrice())
                .beforePrice(data.getClosingPrice().subtract(data.getVersus()))
                .build();
    }

    // 기간 수익률 기반 매핑 (월간/분기/연간 등)
    private IndexPerformanceDto mapToPeriodPerformanceDto(IndexData latest, IndexData earliest) {
        BigDecimal startPrice = earliest.getClosingPrice();
        BigDecimal endPrice = latest.getClosingPrice();
        BigDecimal versus = endPrice.subtract(startPrice);
        BigDecimal fluctuationRate = startPrice.compareTo(BigDecimal.ZERO) != 0
                ? versus.divide(startPrice, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return IndexPerformanceDto.builder()
                .indexInfoId(latest.getIndexInfo().getId())
                .indexClassification(latest.getIndexInfo().getIndexClassification())
                .indexName(latest.getIndexInfo().getIndexName())
                .versus(versus)
                .fluctuationRate(fluctuationRate)
                .currentPrice(endPrice)
                .beforePrice(startPrice)
                .build();
    }

    // 이동평균선 계산
    private List<ChartDataPoint> calculateMovingAverage(List<ChartDataPoint> points, int days) {
        List<ChartDataPoint> maPoints = new ArrayList<>();
        for (int i = days - 1; i < points.size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i - days + 1; j <= i; j++) sum = sum.add(points.get(j).value());
            BigDecimal avg = sum.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
            maPoints.add(new ChartDataPoint(points.get(i).date(), avg));
        }
        return maPoints;
    }
}