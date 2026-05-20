package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.*;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.entity.PeriodType;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class IndexDataService {

    private final IndexDataRepository indexDataRepository;
    private final IndexDataMapper mapper;
    private final IndexInfoRepository indexInfoRepository;

    @Transactional
    public IndexDataDto create(IndexDataCreateRequest request, SourceType sourceType) {

        IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 IndexInfo입니다."));

        IndexData indexData = mapper.toIndexData(request);

        indexData.setIndexInfo(indexInfo);
        indexData.setSourceType(sourceType);

        System.out.println(indexData.getIndexInfo());
        System.out.println(indexData.getIndexInfoId());

        IndexData saved = indexDataRepository.save(indexData);

        return mapper.toDto(saved);
    }

    @Transactional
    public IndexDataDto update(UUID id, IndexDataUpdateRequest patch) {
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

        return mapper.toDto(indexData);
    }

    @Transactional
    public void delete(UUID id) {
        IndexData indexData = indexDataRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제 대상 지수 데이터가 없습니다."));
        indexDataRepository.deleteById(indexData.getId());
    }

    @Transactional(readOnly = true)
    public CursorPageResponseIndexDataDto<IndexDataDto> getIndexDataList(
            IndexDataSearchRequest searchRequest
    ) {
        Slice<IndexData> sliceResult = indexDataRepository.findListByFilterAndCursor(searchRequest);
        List<IndexDataDto> convertedContent = sliceResult.map(mapper::toDto).getContent();

        String nextCursor = null;
        UUID nextIdAfter = null;

        if (sliceResult.hasNext() && !convertedContent.isEmpty()) {
            IndexDataDto lastItem = convertedContent.get(convertedContent.size() - 1);
            nextCursor = lastItem.getCursorValueByField(searchRequest.sortField() == null ? "baseDate":searchRequest.sortField());
            nextIdAfter = lastItem.id() != null ? lastItem.id() : null;
        }

        Integer totalCount = indexDataRepository.countByFilter(searchRequest);

        return new CursorPageResponseIndexDataDto<>(
                convertedContent,
                nextCursor,
                nextIdAfter,
                sliceResult.getSize(),
                totalCount,
                sliceResult.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public List<RankedIndexPerformanceDto> getRank(UUID indexInfoId, PeriodType period, int limit) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = period.getStartDate(endDate);

        List<IndexPerformanceDto> performance = indexDataRepository.findByIndexInfoIdAndBaseDateBetweenOrderByFluctuationRateDesc(
                indexInfoId,startDate,endDate, Limit.of(limit))
                .stream().map(mapper::toPerformanceDto)
                .toList();

        return IntStream.range(0, performance.size())
                .mapToObj(i->{
                    IndexPerformanceDto p = performance.get(i);
                    int rank = i +1;

                    return new RankedIndexPerformanceDto(p, rank);
                })
                .toList();
    }


}
