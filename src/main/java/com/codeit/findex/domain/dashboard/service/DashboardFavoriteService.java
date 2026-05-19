package com.codeit.findex.domain.dashboard.service;

import com.codeit.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.codeit.findex.domain.dashboard.DashboardMapper;
import com.codeit.findex.domain.dashboard.dto.IndexPerformanceResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardFavoriteService {

    private final IndexDataRepository indexDataRepository;
    private final DashboardMapper dashboardMapper;

    public List<DashboardSummaryResponse> getSummary() {

        List<IndexData> all = indexDataRepository.findAll();

        Map<UUID, IndexData> latestMap = new HashMap<>();

        for (IndexData data : all) {
            // 즐겨찾기만 필터
            if (!data.getIndexInfo().getFavorite()) continue;
            UUID key = data.getIndexInfo().getId();
            // 최신 데이터만 유지
            if (!latestMap.containsKey(key) ||
                    data.getBaseDate().isAfter(latestMap.get(key).getBaseDate())) {

                latestMap.put(key, data);
            }
        }

        return latestMap.values().stream()
                .map(dashboardMapper::toDto)
                .toList();
    }

}