package com.codeit.findex.domain.dashboard;

import com.codeit.findex.domain.dashboard.dto.IndexInfoSummaryDto;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "indexClassification", target = "indexClassification")
    @Mapping(source = "indexName", target = "indexName")
    IndexInfoSummaryDto toSummaryDto(IndexInfo indexInfo);

    List<IndexInfoSummaryDto> toSummaryDtoList(List<IndexInfo> indexInfoList);
}