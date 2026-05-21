package com.codeit.findex.domain.autosync.service;

import com.codeit.findex.domain.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.domain.autosync.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.domain.autosync.dto.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;
import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoSyncService {

    private final AutoSyncRepository autoSyncRepository;

    /*
     * 1. 자동 연동 설정 목록 조회 (커서 페이징 + 동적 필터링/정렬 기능 포함)
     */
    public CursorPageResponseAutoSyncConfigDto getAutoSyncConfigs(
            UUID nextIdAfter, UUID indexId, Boolean enabled, String sort, int size) {

        // 다음 페이지 유무 확인
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
}