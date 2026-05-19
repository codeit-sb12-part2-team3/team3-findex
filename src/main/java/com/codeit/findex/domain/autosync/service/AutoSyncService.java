package com.codeit.findex.domain.autosync.service;

import com.codeit.findex.domain.autosync.dto.AutoSyncResponse;
import com.codeit.findex.domain.autosync.dto.AutoSyncUpdateRequest;
import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;

import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoSyncService {

    private final AutoSyncRepository autoSyncRepository;

    public AutoSyncResponse getAutoSyncStatus(UUID indexId) {
        AutoSync autoSync = autoSyncRepository.findByIndexInfo_Id(indexId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return AutoSyncResponse.from(autoSync);
    }

    @Transactional
    public AutoSyncResponse updateAutoSyncStatus(UUID indexId, AutoSyncUpdateRequest request) {
        AutoSync autoSync = autoSyncRepository.findByIndexInfo_Id(indexId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        autoSync.updateEnabled(request.enabled());

        return AutoSyncResponse.from(autoSync);
    }
}