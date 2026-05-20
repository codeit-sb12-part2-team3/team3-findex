package com.codeit.findex.infra.openapi;

import com.codeit.findex.global.exception.ErrorCode;
import com.codeit.findex.global.exception.BusinessException;
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
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl() + "/getStockMarketIndex")
                .queryParam("serviceKey", properties.getServiceKey())
                .queryParam("resultType", "json")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("idxNm", indexName)
                .queryParam("basDt", baseDate)
                .toUriString();

        OpenApiResponseDto response = restClient.get()
                .uri(url)
                .retrieve()
                .body(OpenApiResponseDto.class);

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