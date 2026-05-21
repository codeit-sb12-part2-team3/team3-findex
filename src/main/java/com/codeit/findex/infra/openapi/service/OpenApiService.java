package com.codeit.findex.infra.openapi.service;

import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import com.codeit.findex.infra.openapi.client.OpenApiClient;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import com.codeit.findex.infra.openapi.parser.OpenApiResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final OpenApiClient openApiClient;
    private final OpenApiResponseParser openApiResponseParser;
    private final OpenApiIndexInfoService openApiIndexInfoService;
    private final OpenApiIndexDataService openApiIndexDataService;

    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveAllIndexInfo() {
        long totalStart = System.currentTimeMillis();

        long apiStart = System.currentTimeMillis();
        List<OpenApiIndexItemDto> items = fetchRequiredItems(null, null, 1, 100);
        long apiEnd = System.currentTimeMillis();

        long saveStart = System.currentTimeMillis();
        openApiIndexInfoService.saveAll(items);
        long saveEnd = System.currentTimeMillis();

        long totalEnd = System.currentTimeMillis();

        log.info("Open API 호출 시간: {} ms", apiEnd - apiStart);
        log.info("DB 저장 시간: {} ms", saveEnd - saveStart);
        log.info("Open API 전체 지수 정보 동기화 시간: {} ms, 데이터 수: {}개",
                totalEnd - totalStart,
                items.size()
        );

        return items;
    }

    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveIndexInfo(String indexName) {
        List<OpenApiIndexItemDto> items = fetchRequiredItems(indexName, null, 1, 10);
        openApiIndexInfoService.saveAll(items);
        return items;
    }

    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveIndexData(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        List<OpenApiIndexItemDto> items =
                filterByIndexName(
                        fetchOptionalItems(indexName, baseDate, pageNo, numOfRows),
                        indexName
                );

        if (items.isEmpty()) {
            return List.of();
        }

        openApiIndexInfoService.saveAll(items);
        openApiIndexDataService.saveAll(items);

        return items;
    }

    private List<OpenApiIndexItemDto> fetchRequiredItems(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        List<OpenApiIndexItemDto> items = fetchItems(indexName, baseDate, pageNo, numOfRows);
        validateItems(items);
        return items;
    }

    private List<OpenApiIndexItemDto> fetchOptionalItems(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        List<OpenApiIndexItemDto> items = fetchItems(indexName, baseDate, pageNo, numOfRows);
        return items == null ? List.of() : items;
    }

    private List<OpenApiIndexItemDto> fetchItems(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        OpenApiResponseDto response =
                openApiClient.getStockMarketIndex(indexName, baseDate, pageNo, numOfRows);
        return openApiResponseParser.parseItems(response);
    }

    private List<OpenApiIndexItemDto> filterByIndexName(
            List<OpenApiIndexItemDto> items,
            String indexName
    ) {
        if (indexName == null || indexName.isBlank()) {
            return items;
        }

        return items.stream()
                .filter(item -> item.getIdxNm() != null
                        && item.getIdxNm().contains(indexName))
                .toList();
    }

    private void validateItems(List<OpenApiIndexItemDto> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.OPEN_API_INVALID_RESPONSE);
        }
    }
}