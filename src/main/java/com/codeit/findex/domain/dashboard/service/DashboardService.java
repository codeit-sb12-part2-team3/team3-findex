package com.codeit.findex.domain.dashboard.service;

import com.codeit.findex.domain.dashboard.DashboardMapper;
import com.codeit.findex.domain.dashboard.dto.IndexInfoSummaryDto;
import com.codeit.findex.domain.dashboard.repository.DashboardRepository;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardMapper dashboardMapper;

    /*
     * 대시보드 메인 지수 정보 요약 목록 조회
     */
    public List<IndexInfoSummaryDto> getDashboardSummaries() {
        // 최신 지수 데이터들을 수집
        List<IndexData> latestData = dashboardRepository.findLatestIndexDataWithInfo();

        //
        List<IndexInfo> indexInfos = latestData.stream()
                .map(IndexData::getIndexInfo)
                .filter(info -> info != null)
                .collect(Collectors.toList());

        // Mapper로 DTO 리스트로 변환해서 반환
        return dashboardMapper.toSummaryDtoList(indexInfos);
    }
}