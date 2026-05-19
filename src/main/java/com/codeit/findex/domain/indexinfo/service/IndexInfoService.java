package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexInfoService {

    // 지수 정보 저장 Repository
    private final IndexInfoRepository indexInfoRepository;

    // 지수 정보 등록
    public IndexInfoResponse create(IndexInfoCreateRequest request) {

        // 요청 DTO -> Entity 변환
        IndexInfo indexInfo = IndexInfo.builder()
                .indexName(request.indexName())
                .indexClassification(request.indexClassification())
                .employedItemsCount(request.employedItemsCount())
                .basePointInTime(request.basePointInTime())
                .baseIndex(request.baseIndex())
                .sourceType(request.sourceType())
                .favorite(request.favorite())
                .build();

        // DB 저장
        IndexInfo savedIndexInfo = indexInfoRepository.saveAndFlush(indexInfo);

        // Entity -> Response DTO 변환
        return toResponse(savedIndexInfo);
    }
    // 지수 정보 목록 조회
    @Transactional(readOnly = true)
    public List<IndexInfoResponse> findAll() {
        return indexInfoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 지수 정보 단건 조회
    @Transactional(readOnly = true)
    public IndexInfoResponse findById(UUID id) {

        return toResponse(getIndexInfo(id));
    }

    // 지수 정보 수정
    public IndexInfoResponse update(
            UUID id,
            IndexInfoUpdateRequest request
    ) {
        IndexInfo indexInfo = getIndexInfo(id);

        indexInfo.update(
                request.employedItemsCount(),
                request.basePointInTime(),
                request.baseIndex(),
                request.favorite()
        );

        return toResponse(indexInfo);
    }

    // id 기준 지수 정보 조회
    private IndexInfo getIndexInfo(UUID id) {

        return indexInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }

    // Entity → Response DTO 변환
    private IndexInfoResponse toResponse(IndexInfo indexInfo) {

        return new IndexInfoResponse(
                indexInfo.getId(),
                indexInfo.getIndexName(),
                indexInfo.getIndexClassification(),
                indexInfo.getEmployedItemsCount(),
                indexInfo.getBasePointInTime(),
                indexInfo.getBaseIndex(),
                indexInfo.getSourceType(),
                indexInfo.getFavorite(),
                indexInfo.getCreatedAt(),
                indexInfo.getUpdatedAt()
        );
    }
}