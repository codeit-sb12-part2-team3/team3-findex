package com.codeit.findex.domain.autosync.repository;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutoSyncRepository extends JpaRepository<AutoSync, UUID>, AutoSyncRepositoryCustom {

    Optional<AutoSync> findByIndexInfo_Id(UUID indexId);

    // 스케줄러용: indexInfo를 JOIN FETCH로 함께 로딩 (LazyInitializationException 방지)
    @Query("SELECT a FROM AutoSync a JOIN FETCH a.indexInfo WHERE a.enabled = true")
    List<AutoSync> findByEnabledTrueWithIndexInfo();

}