package com.codeit.findex.domain.syncjob.repository;

import com.codeit.findex.domain.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SyncJobRepository extends JpaRepository<SyncJob, UUID>,
        JpaSpecificationExecutor<SyncJob> {
}
