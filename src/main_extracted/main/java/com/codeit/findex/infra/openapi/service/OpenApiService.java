package com.codeit.findex.infra.openapi.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import com.codeit.findex.infra.openapi.client.OpenApiClient;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import com.codeit.findex.infra.openapi.parser.OpenApiResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final OpenApiClient openApiClient;
    private final OpenApiResponseParser openApiResponseParser;
    private final OpenApiIndexInfoService openApiIndexInfoService;
    private final OpenApiIndexDataService openApiIndexDataService;

    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveAllIndexInfo() {
        List<OpenApiIndexItemDto> items = fetchRequiredItems(null, null, 1, 100);
        openApiIndexInfoService.saveAll(items);
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

        for (OpenApiIndexItemDto item : items) {
            IndexInfoResponse indexInfo = openApiIndexInfoService.saveOrUpdate(item);
            openApiIndexDataService.save(indexInfo, item);
        }

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