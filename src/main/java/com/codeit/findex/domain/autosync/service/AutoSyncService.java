package com.codeit.findex.domain.autosync.service;

import com.codeit.findex.domain.autosync.dto.AutoSyncCreateRequest;
import com.codeit.findex.domain.autosync.dto.AutoSyncResponse;
import com.codeit.findex.domain.autosync.dto.AutoSyncUpdateRequest;
import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
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
    private final IndexInfoRepository indexInfoRepository;

    public AutoSyncResponse getAutoSyncStatus(UUID indexId) {
        AutoSync autoSync = autoSyncRepository.findByIndexInfo_Id(indexId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return AutoSyncResponse.from(autoSync);
    }

    @Transactional
    public AutoSyncResponse createAutoSync(AutoSyncCreateRequest request) {
        // 중복 방지
        if (autoSyncRepository.findByIndexInfo_Id(request.indexId()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        // indexinfo 테이블 검사
        IndexInfo indexInfo = indexInfoRepository.findById(request.indexId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        // 엔티티 생성
        AutoSync newAutoSync = new AutoSync(indexInfo, false);

        AutoSync savedAutoSync = autoSyncRepository.save(newAutoSync);

        return AutoSyncResponse.from(savedAutoSync);
    }

    @Transactional
    public AutoSyncResponse updateAutoSyncStatus(UUID indexId, AutoSyncUpdateRequest request) {
        AutoSync autoSync = autoSyncRepository.findByIndexInfo_Id(indexId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        autoSync.updateEnabled(request.enabled());

        return AutoSyncResponse.from(autoSync);
    }
}