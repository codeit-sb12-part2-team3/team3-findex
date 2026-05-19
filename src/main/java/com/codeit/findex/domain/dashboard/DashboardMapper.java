package com.codeit.findex.domain.dashboard;

import com.codeit.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    @Mapping(source = "indexInfo.id", target = "indexId")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.favorite", target = "favorite")
    DashboardSummaryResponse toDto(IndexData indexData);

    List<DashboardSummaryResponse> toDtoList(List<IndexData> indexDataList);
}