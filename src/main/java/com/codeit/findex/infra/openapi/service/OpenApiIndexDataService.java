package com.codeit.findex.infra.openapi.service;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenApiIndexDataService {

    private static final DateTimeFormatter BASE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;

    @Transactional
    public void saveAll(List<OpenApiIndexItemDto> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        List<String> indexNames = items.stream()
                .map(OpenApiIndexItemDto::getIdxNm)
                .distinct()
                .toList();

        Map<String, IndexInfo> indexInfoMap = indexInfoRepository.findByIndexNameIn(indexNames)
                .stream()
                .collect(Collectors.toMap(
                        IndexInfo::getIndexName,
                        indexInfo -> indexInfo
                ));

        List<UUID> indexInfoIds = indexInfoMap.values()
                .stream()
                .map(IndexInfo::getId)
                .distinct()
                .toList();

        List<LocalDate> baseDates = items.stream()
                .map(item -> LocalDate.parse(item.getBasDt(), BASE_DATE_FORMAT))
                .distinct()
                .toList();

        Map<String, IndexData> existingDataMap =
                indexDataRepository.findByIndexInfoIdInAndBaseDateIn(indexInfoIds, baseDates)
                        .stream()
                        .collect(Collectors.toMap(
                                data -> data.getIndexInfoId() + "_" + data.getBaseDate(),
                                data -> data
                        ));

        List<IndexData> newIndexDataList = new ArrayList<>();

        for (OpenApiIndexItemDto item : items) {
            IndexInfo indexInfo = indexInfoMap.get(item.getIdxNm());

            if (indexInfo == null) {
                continue;
            }

            LocalDate baseDate = LocalDate.parse(item.getBasDt(), BASE_DATE_FORMAT);
            String key = indexInfo.getId() + "_" + baseDate;

            IndexData existingData = existingDataMap.get(key);

            if (existingData != null) {
                existingData.updateValues(
                        toBigDecimal(item.getMkp()),
                        toBigDecimal(item.getClpr()),
                        toBigDecimal(item.getHipr()),
                        toBigDecimal(item.getLopr()),
                        toBigDecimal(item.getVs()),
                        toBigDecimal(item.getFltRt()),
                        toLong(item.getTrqu()),
                        toLong(item.getTrPrc()),
                        toLong(item.getLstgMrktTotAmt())
                );
            } else {
                IndexData newIndexData = IndexData.builder()
                        .indexInfo(indexInfo)
                        .baseDate(baseDate)
                        .sourceType(SourceType.OPEN_API)
                        .marketPrice(toBigDecimal(item.getMkp()))
                        .closingPrice(toBigDecimal(item.getClpr()))
                        .highPrice(toBigDecimal(item.getHipr()))
                        .lowPrice(toBigDecimal(item.getLopr()))
                        .versus(toBigDecimal(item.getVs()))
                        .fluctuationRate(toBigDecimal(item.getFltRt()))
                        .tradingQuantity(toLong(item.getTrqu()))
                        .tradingPrice(toLong(item.getTrPrc()))
                        .marketTotalAmount(toLong(item.getLstgMrktTotAmt()))
                        .build();

                newIndexDataList.add(newIndexData);
                existingDataMap.put(key, newIndexData);
            }
        }

        if (!newIndexDataList.isEmpty()) {
            indexDataRepository.saveAll(newIndexDataList);
        }
    }

    private BigDecimal toBigDecimal(String value) {
        return value == null || value.isBlank()
                ? BigDecimal.ZERO
                : new BigDecimal(value);
    }

    private Long toLong(String value) {
        return value == null || value.isBlank()
                ? 0L
                : Long.parseLong(value);
    }
}