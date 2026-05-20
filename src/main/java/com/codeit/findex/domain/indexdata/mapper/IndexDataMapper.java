package com.codeit.findex.domain.indexdata.mapper;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    IndexDataResponse toResponse(IndexData entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "indexInfo", ignore = true)
    @Mapping(target = "sourceType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    IndexData toIndexData(IndexDataCreateRequest request);

}