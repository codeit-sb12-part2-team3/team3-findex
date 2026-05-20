package com.codeit.findex.domain.dashboard;

import com.codeit.findex.domain.dashboard.dto.DashboardSummaryResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "versus", target = "versus")
    @Mapping(source = "fluctuationRate", target = "fluctuationRate")
    @Mapping(source = "closingPrice", target = "currentPrice")
    @Mapping(expression = "java(indexData.getClosingPrice().subtract(indexData.getVersus()))", target = "beforePrice")
    DashboardSummaryResponse toDto(IndexData indexData);

    List<DashboardSummaryResponse> toDtoList(List<IndexData> indexDataList);
}