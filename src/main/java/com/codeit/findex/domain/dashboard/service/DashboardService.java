package com.codeit.findex.domain.dashboard.service;

import com.codeit.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.codeit.findex.domain.dashboard.DashboardMapper;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final IndexDataRepository indexDataRepository;
    private final DashboardMapper dashboardMapper;

    public List<DashboardSummaryResponse> getFavoriteIndexSummaries() {

        List<IndexData> latestDataList = indexDataRepository.findLatestFavoriteIndexData();

        return dashboardMapper.toDtoList(latestDataList);
    }
}