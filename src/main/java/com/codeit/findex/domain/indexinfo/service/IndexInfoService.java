package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return new IndexInfoResponse(
                savedIndexInfo.getId(),
                savedIndexInfo.getIndexName(),
                savedIndexInfo.getIndexClassification(),
                savedIndexInfo.getEmployedItemsCount(),
                savedIndexInfo.getBasePointInTime(),
                savedIndexInfo.getBaseIndex(),
                savedIndexInfo.getSourceType(),
                savedIndexInfo.getFavorite(),
                savedIndexInfo.getCreatedAt(),
                savedIndexInfo.getUpdatedAt()
        );
    }
}