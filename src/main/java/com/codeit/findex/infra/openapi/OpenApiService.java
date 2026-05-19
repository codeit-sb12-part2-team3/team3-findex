package com.codeit.findex.infra.openapi;


import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import com.codeit.findex.infra.openapi.parser.OpenApiResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OpenApiService {

    private final OpenApiClient openApiClient;
    private final OpenApiResponseParser openApiResponseParser;

    public List<OpenApiIndexItemDto> getStockMarketIndex(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        OpenApiResponseDto response = openApiClient.getStockMarketIndex(
                indexName,
                baseDate,
                pageNo,
                numOfRows
        );

        return openApiResponseParser.parseItems(response);

    }
}
