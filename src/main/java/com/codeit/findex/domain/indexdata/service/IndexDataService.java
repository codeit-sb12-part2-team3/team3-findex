package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.*;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndexDataService {

    private final IndexDataRepository indexDataRepository;
    private final IndexDataMapper mapper;

    @Transactional
    public IndexDataResponse create(IndexDataCreateRequest newIndexData, SourceType sourceType) {
        IndexData indexData = mapper.toIndexData(newIndexData);
        indexData.setSourceType(sourceType);
        indexData = indexDataRepository.save(indexData);

        return mapper.toResponse(indexData);
    }

    @Transactional
    public IndexDataResponse update(UUID id, IndexDataUpdateRequest patch) {
        IndexData indexData = indexDataRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정 대상 지수 데이터가 없습니다."));

        if (patch.marketPrice() != null) indexData.setMarketPrice(patch.marketPrice());
        if (patch.closingPrice() != null) indexData.setClosingPrice(patch.closingPrice());
        if (patch.highPrice() != null) indexData.setHighPrice(patch.highPrice());
        if (patch.lowPrice() != null) indexData.setLowPrice(patch.lowPrice());
        if (patch.versus() != null) indexData.setVersus(patch.versus());
        if (patch.fluctuationRate() != null) indexData.setFluctuationRate(patch.fluctuationRate());
        if (patch.tradingQuantity() != null) indexData.setTradingQuantity(patch.tradingQuantity());
        if (patch.tradingPrice() != null) indexData.setTradingPrice(patch.tradingPrice());
        if (patch.marketTotalAmount() != null) indexData.setMarketTotalAmount(patch.marketTotalAmount());

        return mapper.toResponse(indexData);
    }

    @Transactional
    public void delete(UUID id) {
        IndexData indexData = indexDataRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제 대상 지수 데이터가 없습니다."));
        indexDataRepository.deleteById(indexData.getId());
    }

    @Transactional(readOnly = true)
    public IndexDataSearchResponse<IndexDataResponse> getIndexDataList(
            IndexDataSearchRequest searchRequest
    ) {
        Slice<IndexData> sliceResult = indexDataRepository.findListByFilterAndCursor(searchRequest);
        List<IndexDataResponse> convertedContent = sliceResult.map(mapper::toResponse).getContent();

        String nextCursor = null;
        String nextIdAfter = null;

        if (sliceResult.hasNext() && !convertedContent.isEmpty()) {
            IndexDataResponse lastItem = convertedContent.get(convertedContent.size() - 1);
            nextCursor = lastItem.getCursorValueByField(searchRequest.sortField() == null ? "baseDate":searchRequest.sortField());
            nextIdAfter = lastItem.id() != null ? lastItem.id().toString() : null;
        }

        Integer totalCount = indexDataRepository.countByFilter(searchRequest);

        return new IndexDataSearchResponse<>(
                convertedContent,
                nextCursor,
                nextIdAfter,
                sliceResult.getSize(),
                totalCount,
                sliceResult.hasNext()
        );
    }


}
