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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        IndexInfo indexInfo = buildIndexInfo(request, sourceType);
        IndexInfo saved = indexInfoRepository.save(indexInfo);
        return toResponse(saved);
    }
    // Open API 지수 정보 동기화
    public IndexInfoResponse syncIndexInfo(IndexInfoCreateRequest request) {
        return indexInfoRepository.findByIndexName(request.indexName())
                .map(indexInfo -> {
                    // 업데이트 시에도 기존 값 유지
                    updateExistingIndexInfo(indexInfo, request);

                    return toResponse(indexInfo);
                })
                .orElseGet(() -> createFromOpenApi(request));
    }

    public List<IndexInfoResponse> syncIndexInfos(List<IndexInfoCreateRequest> requests) {
        List<String> indexNames = requests.stream()
                .map(IndexInfoCreateRequest::indexName)
                .distinct()
                .toList();

        Map<String, IndexInfo> existingMap = indexInfoRepository.findByIndexNameIn(indexNames)
                .stream()
                .collect(Collectors.toMap(
                        IndexInfo::getIndexName,
                        indexInfo -> indexInfo
                ));

        List<IndexInfo> newIndexInfos = new ArrayList<>();

        for (IndexInfoCreateRequest request : requests) {
            IndexInfo existing = existingMap.get(request.indexName());

            if (existing != null) {
                updateExistingIndexInfo(existing, request);
            } else {
                IndexInfo newIndexInfo = buildIndexInfo(request, SourceType.OPEN_API);
                newIndexInfos.add(newIndexInfo);
                existingMap.put(request.indexName(), newIndexInfo);
            }
        }

        if (!newIndexInfos.isEmpty()) {
            indexInfoRepository.saveAll(newIndexInfos);
        }

        return existingMap.values()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 지수 정보 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<IndexInfoResponse> findAll(
            String indexClassification,
            String indexName,
            Boolean favorite
    ) {

        List<IndexInfoResponse> content = indexInfoRepository
                .search(
                        indexClassification,
                        indexName,
                        favorite,
                        PageRequest.of(0, 100)
                )
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
        indexInfoRepository.flush();
    }

    private IndexInfo buildIndexInfo(IndexInfoCreateRequest request, SourceType sourceType) {
        LocalDate safeBasePointInTime = request.basePointInTime() != null
                ? request.basePointInTime()
                : LocalDate.of(1900, 1, 1);

        BigDecimal safeBaseIndex = request.baseIndex() != null
                ? request.baseIndex()
                : new BigDecimal("100.00");

        Boolean safeFavorite = request.favorite() != null
                ? request.favorite()
                : false;

        return IndexInfo.builder()
                .indexName(request.indexName())
                .indexClassification(request.indexClassification())
                .employedItemsCount(request.employedItemsCount())
                .basePointInTime(safeBasePointInTime)
                .baseIndex(safeBaseIndex)
                .sourceType(sourceType)
                .favorite(safeFavorite)
                .build();
    }

    private void updateExistingIndexInfo(IndexInfo indexInfo, IndexInfoCreateRequest request) {
        BigDecimal safeBaseIndex = request.baseIndex() != null
                ? request.baseIndex()
                : indexInfo.getBaseIndex();

        LocalDate safeBasePointInTime = request.basePointInTime() != null
                ? request.basePointInTime()
                : indexInfo.getBasePointInTime();

        indexInfo.updateMarketInfo(
                request.employedItemsCount(),
                safeBasePointInTime,
                safeBaseIndex,
                indexInfo.getFavorite()
        );
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