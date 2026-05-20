package com.codeit.findex.domain.indexdata.mapper;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    IndexDataResponse toResponse(IndexData entity);

    IndexData toIndexData(IndexDataCreateRequest request);

}