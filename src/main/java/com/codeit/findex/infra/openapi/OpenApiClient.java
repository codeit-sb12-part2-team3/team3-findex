package com.codeit.findex.infra.openapi;

import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import com.codeit.findex.infra.openapi.config.OpenApiProperties;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private final RestClient restClient;
    private final OpenApiProperties properties;

    public OpenApiResponseDto getStockMarketIndex(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/getStockMarketIndex");

        builder.queryParam("serviceKey", properties.getServiceKey());
        builder.queryParam("resultType", "json");
        builder.queryParam("pageNo", pageNo);
        builder.queryParam("numOfRows", numOfRows);

        if (indexName != null && !indexName.isBlank()) {
            builder.queryParam("idxNm", indexName);
        }

        if (baseDate != null && !baseDate.isBlank()) {
            builder.queryParam("basDt", baseDate);
        }

        String url = builder.build(false).toUriString();

        OpenApiResponseDto response;

        try {
            String rawResponse = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            System.out.println("=================================");
            System.out.println("OpenAPI URL = " + url);
            System.out.println("OpenAPI RAW RESPONSE = ");
            System.out.println(rawResponse);
            System.out.println("=================================");

            response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(OpenApiResponseDto.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.OPEN_API_REQUEST_FAILED);
        }

        if (response == null || response.getResponse() == null) {
            throw new BusinessException(ErrorCode.OPEN_API_NO_RESPONSE);
        }

        if (response.getResponse().getHeader() == null) {
            throw new BusinessException(ErrorCode.OPEN_API_INVALID_RESPONSE);
        }

        String resultCode = response.getResponse()
                .getHeader()
                .getResultCode();

        if (!"00".equals(resultCode)) {
            throw new BusinessException(ErrorCode.OPEN_API_REQUEST_FAILED);
        }

        return response;
    }
}