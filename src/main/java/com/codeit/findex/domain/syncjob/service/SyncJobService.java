package com.codeit.findex.domain.syncjob.service;

import com.codeit.findex.domain.syncjob.dto.SyncJobListResponse;
import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.domain.syncjob.specification.SyncJobSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SyncJobService {

    private final SyncJobRepository syncJobRepository;

    public Slice<SyncJobListResponse> getSyncJobList(
            SyncJobSearchCondition condition, LocalDateTime lastJobTime, int size
    ) {


        Sort sort = Sort.by(Sort.Direction.DESC, "jobTime");

        Pageable pageable = PageRequest.of(0, size, sort);

        Specification<SyncJob> spec = SyncJobSpecification
                .withCondition(condition)
                .and(SyncJobSpecification.cursor(lastJobTime));

        Slice<SyncJob> slice = syncJobRepository.findAll(spec, pageable);

        return slice.map(SyncJobListResponse::from);

    }



}
