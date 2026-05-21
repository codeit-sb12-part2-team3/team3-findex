package com.codeit.findex.domain.indexdata.service;

import com.codeit.findex.domain.indexdata.dto.*;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.mapper.IndexDataMapper;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codeit.findex.domain.indexdata.entity.PeriodType;

import java.io.PrintWriter;
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

    @Transactional
    public IndexDataDto create(IndexDataCreateRequest newIndexData, SourceType sourceType) {
        IndexData indexData = mapper.toIndexData(newIndexData);
        indexData.setSourceType(sourceType);
        indexData = indexDataRepository.save(indexData);
        return mapper.toDto(indexData);
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
    public CursorPageResponseIndexDataDto getIndexDataList(
            IndexDataSearchRequest searchRequest
    ) {
        Slice<IndexData> sliceResult = indexDataRepository.findListByFilterAndCursor(searchRequest);
        List<IndexDataDto> convertedContent = sliceResult.map(mapper::toDto).getContent();

        String nextCursor = null;
        UUID nextIdAfterUuid = null;

        if (sliceResult.hasNext() && !convertedContent.isEmpty()) {
            IndexDataDto lastItem = convertedContent.get(convertedContent.size() - 1);
            nextCursor = lastItem.getCursorValueByField(searchRequest.sortField());
            nextIdAfterUuid = lastItem.id() != null ? lastItem.id() : null;
        }

        Integer totalCount = indexDataRepository.countByFilter(searchRequest);

        return new CursorPageResponseIndexDataDto(
                convertedContent,
                nextCursor,
                nextIdAfterUuid,
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

    @Transactional(readOnly = true)
    public void exportCsv(IndexDataSearchRequest searchRequest, PrintWriter writer) {
        writer.println("id,indexInfoId,baseDate,sourceType,marketPrice,closingPrice,highPrice,lowPrice,versus,fluctuationRate,tradingQuantity,tradingPrice,marketTotalAmount");

        int totalCount = indexDataRepository.countByFilter(searchRequest);

        // 전체 조회 (페이지네이션 없이)
        IndexDataSearchRequest fullRequest = new IndexDataSearchRequest(
                searchRequest.indexInfoId(),
                searchRequest.startDate(),
                searchRequest.endDate(),
                searchRequest.sortField(),
                searchRequest.sortDirection(),
                null, null, totalCount
        );

        indexDataRepository.findListByFilterAndCursor(fullRequest).getContent()
                .stream()
                .map(mapper::toDto)
                .forEach(d -> writer.println(String.join(",",
                        str(d.id()), str(d.indexInfoId()), str(d.baseDate()),
                        str(d.sourceType()), str(d.marketPrice()), str(d.closingPrice()),
                        str(d.highPrice()), str(d.lowPrice()), str(d.versus()),
                        str(d.fluctuationRate()), str(d.tradingQuantity()),
                        str(d.tradingPrice()), str(d.marketTotalAmount())
                )));

        writer.flush();
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }

}