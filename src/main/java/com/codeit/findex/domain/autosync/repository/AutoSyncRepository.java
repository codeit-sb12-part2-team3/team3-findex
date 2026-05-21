package com.codeit.findex.domain.autosync.repository;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutoSyncRepository extends JpaRepository<AutoSync, UUID>, AutoSyncRepositoryCustom {

    Optional<AutoSync> findByIndexInfo_Id(UUID indexId);

    // 스케줄러 사용 시 활성화
    List<AutoSync> findByEnabledTrue();

}