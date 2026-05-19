package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataResponse;
import com.codeit.findex.domain.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndexDataService {

    private final IndexDataRepository indexDataRepository;

    @Transactional
    public IndexDataResponse create(IndexDataCreateRequest newIndexData) {
        IndexData indexData = IndexData.builder()
                .indexId(newIndexData.indexId())
                .baseDate(newIndexData.baseDate())
                .sourceType(newIndexData.sourceType())
                .marketPrice(newIndexData.marketPrice())
                .closingPrice(newIndexData.closingPrice())
                .highPrice(newIndexData.highPrice())
                .lowPrice(newIndexData.lowPrice())
                .versus(newIndexData.versus())
                .fluctuationRate(newIndexData.fluctuationRate())
                .tradeQuantity(newIndexData.tradeQuantity())
                .tradePrice(newIndexData.tradePrice())
                .marketTotalAmount(newIndexData.marketTotalAmount())
                .build();
        indexData = indexDataRepository.save(indexData);

        return toIndexDataResponse(indexData);
    }

    private IndexDataResponse toIndexDataResponse(IndexData indexData) {
        return new IndexDataResponse(
                indexData.getId(),
                indexData.getIndexId(),
                indexData.getBaseDate(),
                indexData.getSourceType(),
                indexData.getMarketPrice(),
                indexData.getClosingPrice(),
                indexData.getHighPrice(),
                indexData.getLowPrice(),
                indexData.getVersus(),
                indexData.getFluctuationRate(),
                indexData.getTradeQuantity(),
                indexData.getTradePrice(),
                indexData.getMarketTotalAmount()
        );
    }

    @Transactional
    public IndexDataResponse update(UUID id, IndexDataUpdateRequest patch) {
        IndexData indexData = indexDataRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정 대상 지수 데이터가 없습니다."));

        if (patch.sourceType() != null) indexData.setSourceType(patch.sourceType());
        if (patch.marketPrice() != null) indexData.setMarketPrice(patch.marketPrice());
        if (patch.closingPrice() != null) indexData.setClosingPrice(patch.closingPrice());
        if (patch.highPrice() != null) indexData.setHighPrice(patch.highPrice());
        if (patch.lowPrice() != null) indexData.setLowPrice(patch.lowPrice());
        if (patch.versus() != null) indexData.setVersus(patch.versus());
        if (patch.fluctuationRate() != null) indexData.setFluctuationRate(patch.fluctuationRate());
        if (patch.tradeQuantity() != null) indexData.setTradeQuantity(patch.tradeQuantity());
        if (patch.tradePrice() != null) indexData.setTradePrice(patch.tradePrice());
        if (patch.marketTotalAmount() != null) indexData.setMarketTotalAmount(patch.marketTotalAmount());

        return toIndexDataResponse(indexData);
    }




}
