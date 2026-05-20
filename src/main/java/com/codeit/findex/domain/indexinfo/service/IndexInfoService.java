package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.global.common.dto.CursorPageResponse;
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
                .sourceType(SourceType.USER)
                .favorite(request.favorite())
                .build();

        IndexInfo saved = indexInfoRepository.saveAndFlush(indexInfo);

        return toResponse(saved);
    }

    //  지수 정보 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<IndexInfoResponse> findAll() {

        List<IndexInfoResponse> content = indexInfoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return new CursorPageResponse<>(
                content,          // 데이터
                null,             // nextCursor
                null,             // nextIdAfter
                content.size(),   // size
                content.size(),   // totalElements
                false             // hasNext
        );
    }

    @Transactional(readOnly = true)
    public List<IndexInfoResponse> getSummaries() {
        return indexInfoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // 단건 조회
    @Transactional(readOnly = true)
    public IndexInfoResponse findById(UUID id) {
        return toResponse(getIndexInfo(id));
    }

    // 수정
    public IndexInfoResponse update(UUID id, IndexInfoUpdateRequest request) {

        IndexInfo indexInfo = getIndexInfo(id);

        indexInfo.update(
                request.employedItemsCount(),
                request.basePointInTime(),
                request.baseIndex(),
                request.favorite()
        );

        return toResponse(indexInfo);
    }

    // 삭제
    public void delete(UUID id) {
        getIndexInfo(id);
        indexInfoRepository.deleteById(id);
        indexInfoRepository.flush();
    }

    // 내부 조회
    private IndexInfo getIndexInfo(UUID id) {
        return indexInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }

    // Entity → DTO
    private IndexInfoResponse toResponse(IndexInfo indexInfo) {
        return new IndexInfoResponse(
                indexInfo.getId(),
                indexInfo.getIndexName(),
                indexInfo.getIndexClassification(),
                indexInfo.getEmployedItemsCount(),
                indexInfo.getBasePointInTime(),
                indexInfo.getBaseIndex(),
                indexInfo.getSourceType().name(),
                indexInfo.getFavorite(),
                indexInfo.getCreatedAt(),
                indexInfo.getUpdatedAt()
        );
    }
}