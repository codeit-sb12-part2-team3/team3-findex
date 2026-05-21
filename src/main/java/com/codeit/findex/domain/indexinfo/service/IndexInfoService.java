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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;

    // 사용자 등록 지수 정보
    public IndexInfoResponse create(IndexInfoCreateRequest request) {
        return create(request, SourceType.USER);
    }

    // Open API 동기화 지수 정보
    public IndexInfoResponse createFromOpenApi(IndexInfoCreateRequest request) {
        return create(request, SourceType.OPEN_API);
    }

    // 요청 DTO -> Entity 변환
    private IndexInfoResponse create(IndexInfoCreateRequest request, SourceType sourceType) {

        if (indexInfoRepository.existsByIndexName(request.indexName())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // null 방지
        LocalDate safeBasePointInTime = request.basePointInTime() != null
                ? request.basePointInTime()
                : LocalDate.of(1900, 1, 1); // 임의의 기준일 설정

        BigDecimal safeBaseIndex = request.baseIndex() != null
                ? request.baseIndex()
                : new BigDecimal("100.00"); // 임의의 기준 지수 설정

        Boolean safeFavorite = request.favorite() != null
                ? request.favorite()
                : false; // 즐겨찾기 기본값

        IndexInfo indexInfo = IndexInfo.builder()
                .indexName(request.indexName())
                .indexClassification(request.indexClassification())
                .employedItemsCount(request.employedItemsCount())
                .basePointInTime(safeBasePointInTime) // 안전한 값 대입
                .baseIndex(safeBaseIndex)             // 안전한 값 대입
                .sourceType(sourceType)
                .favorite(safeFavorite)               // 안전한 값 대입
                .build();

        IndexInfo saved = indexInfoRepository.saveAndFlush(indexInfo);

        return toResponse(saved);
    }

    // Open API 지수 정보 동기화
    public IndexInfoResponse syncIndexInfo(IndexInfoCreateRequest request) {
        return indexInfoRepository.findByIndexName(request.indexName())
                .map(indexInfo -> {
                    // 업데이트 시에도 기존 값 유지
                    BigDecimal safeBaseIndex = request.baseIndex() != null
                            ? request.baseIndex()
                            : indexInfo.getBaseIndex();

                    indexInfo.update(
                            request.employedItemsCount(),
                            indexInfo.getBasePointInTime(),
                            safeBaseIndex,
                            indexInfo.getFavorite()
                    );
                    return toResponse(indexInfo);
                })
                .orElseGet(() -> createFromOpenApi(request));
    }

    // 지수 정보 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<IndexInfoResponse> findAll(
            String indexClassification,
            String indexName,
            Boolean favorite
    ) {
        String safeClassification = (indexClassification == null || indexClassification.isBlank()) ? null : indexClassification;
        String safeName = (indexName == null || indexName.isBlank()) ? null : indexName;

        List<IndexInfoResponse> content = indexInfoRepository
                .findAllByFilter(safeClassification, safeName, favorite)
                .stream()
                .map(this::toResponse)
                .toList();

        return new CursorPageResponse<>(
                content,
                null,
                null,
                content.size(),
                (long) content.size(),
                false
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

        indexInfo.updateMarketInfo(
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
        indexInfoRepository.flush(); // 즉시 삭제 반영
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