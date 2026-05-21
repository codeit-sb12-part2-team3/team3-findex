package com.codeit.findex.domain.autosync.service;

import com.codeit.findex.domain.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.domain.autosync.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.domain.autosync.dto.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoSyncService {

    private final AutoSyncRepository autoSyncRepository;

    private final IndexInfoRepository indexInfoRepository;

    /*
     * 1. 자동 연동 설정 목록 조회 (커서 페이징 + 동적 필터링/정렬 기능 포함)
     */
    @Transactional
    public CursorPageResponseAutoSyncConfigDto getAutoSyncConfigs(
            UUID nextIdAfter, UUID indexId, Boolean enabled, String sort, int size) {

        // 화면에 그리기 전에, 누락된 AutoSync 데이터가 있으면 채워 넣기!
        syncAutoSyncDataWithIndexInfo();

        // 조회 로직 실행
        List<AutoSync> entities = autoSyncRepository.findListByFilterAndCursor(
                nextIdAfter, indexId, enabled, sort, size + 1
        );

        // 다음 페이지가 존재하는지 판별
        boolean hasNext = entities.size() > size;

        // 프론트 사이즈 만큼만 리스트 자르기
        List<AutoSync> contentEntities = hasNext ? entities.subList(0, size) : entities;

        // DTO 리스트 변환
        List<AutoSyncConfigDto> content = contentEntities.stream()
                .map(AutoSyncConfigDto::from)
                .collect(Collectors.toList());

        // 다음 커서 정보 세팅
        UUID newNextIdAfter = null;
        String nextCursor = null;
        if (!content.isEmpty() && hasNext) {
            newNextIdAfter = content.get(content.size() - 1).id();
            nextCursor = newNextIdAfter.toString();
        }

        // 조건 충족 데이터 카운트
        long totalElements = autoSyncRepository.countByFilter(indexId, enabled);


        return CursorPageResponseAutoSyncConfigDto.of(
                content,
                nextCursor,
                newNextIdAfter,
                content.size(), // 현재 페이지에 담긴 실제 데이터 개수
                totalElements,
                hasNext
        );
    }

    /*
     * 2. 자동 연동 활성화 여부 변경 (토글 스위치)
     */
    @Transactional
    public AutoSyncConfigDto updateAutoSyncStatus(UUID id, AutoSyncConfigUpdateRequest request) {

        // 대상 엔티티 조회
        AutoSync autoSync = autoSyncRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // JPA 더티 체킹
        autoSync.updateEnabled(request.enabled());

        return AutoSyncConfigDto.from(autoSync);
    }


    private void syncAutoSyncDataWithIndexInfo() {
        // 전체 지수 목록 가져오기
        List<IndexInfo> allIndexes = indexInfoRepository.findAll();
        // 현재 생성되어 있는 AutoSync의 지수 ID 목록 가져오기
        List<UUID> existingAutoSyncIndexIds = autoSyncRepository.findAll().stream()
                .map(sync -> sync.getIndexInfo().getId())
                .collect(Collectors.toList());

        List<AutoSync> newAutoSyncs = new ArrayList<>();

        for (IndexInfo indexInfo : allIndexes) {
            // 아직 AutoSync 설정이 안 만들어진 지수라면 새로 생성
            if (!existingAutoSyncIndexIds.contains(indexInfo.getId())) {
                AutoSync newSync = AutoSync.builder()
                        .indexInfo(indexInfo)
                        .enabled(false) // 초기값은 '비활성화'
                        .build();
                newAutoSyncs.add(newSync);
            }
        }

        // 새로 만들어야 할 설정들이 있다면 한 번에 DB에 저장
        if (!newAutoSyncs.isEmpty()) {
            autoSyncRepository.saveAll(newAutoSyncs);
        }
    }
}