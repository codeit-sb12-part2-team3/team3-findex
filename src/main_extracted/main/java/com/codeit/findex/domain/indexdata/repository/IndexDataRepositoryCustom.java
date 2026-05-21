package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.dto.IndexDataSearchRequest;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.domain.Slice;

public interface IndexDataRepositoryCustom {

    Slice<IndexData> findListByFilterAndCursor(IndexDataSearchRequest searchRequest);

    Integer countByFilter(IndexDataSearchRequest request);

//    Slice<IndexData> searchByCondition(IndexDataSearchRequest request);
}
