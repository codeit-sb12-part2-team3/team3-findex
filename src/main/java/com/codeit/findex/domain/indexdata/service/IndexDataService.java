package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataResponse;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
