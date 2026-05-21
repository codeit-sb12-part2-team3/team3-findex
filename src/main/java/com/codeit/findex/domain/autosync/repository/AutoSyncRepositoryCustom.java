package com.codeit.findex.domain.autosync.repository;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import java.util.List;
import java.util.UUID;

public interface AutoSyncRepositoryCustom {

    List<AutoSync> findListByFilterAndCursor(UUID nextIdAfter, UUID indexId, Boolean enabled, String sort, int size);

    // 검색 조건에 맞는 전체 데이터 개수 조회
    long countByFilter(UUID indexId, Boolean enabled);
}