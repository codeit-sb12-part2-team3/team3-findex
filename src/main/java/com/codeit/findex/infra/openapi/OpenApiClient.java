package com.codeit.findex.infra.openapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private final RestTemplate restTemplate;
    private final OpenApiProperties properties;

    public OpenApiResponseDto getStockMarketIndex(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/getStockMarketIndex")
                .queryParam("serviceKey", properties.getServiceKey())
                .queryParam("resultType", "json")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("idxNm", indexName)
                .queryParam("basDt", baseDate)
                .toUriString();

        return restTemplate.getForObject(url, OpenApiResponseDto.class);
    }
}