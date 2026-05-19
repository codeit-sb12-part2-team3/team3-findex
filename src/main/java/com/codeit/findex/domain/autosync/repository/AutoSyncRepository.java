package com.codeit.findex.domain.autosync.repository;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutoSyncRepository extends JpaRepository<AutoSync, UUID> {

    Optional<AutoSync> findByIndexInfo_Id(UUID indexId);
    List<AutoSync> findByEnabledTrue();
}