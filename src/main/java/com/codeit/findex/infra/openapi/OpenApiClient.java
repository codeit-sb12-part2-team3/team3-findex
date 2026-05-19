package com.codeit.findex.infra.openapi;

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
            throw new RuntimeException("Open API 응답이 없습니다.");
        }

        if (response.getResponse().getHeader() == null) {
            throw new RuntimeException("Open API 응답 header가 없습니다.");
        }

        String resultCode = response.getResponse()
                .getHeader()
                .getResultCode();

        if (!"00".equals(resultCode)) {
            String resultMsg = response.getResponse()
                    .getHeader()
                    .getResultMsg();
            throw new RuntimeException("Open API 요청 실패: " + resultMsg);
        }

        if (response.getResponse().getBody() == null) {
            throw new RuntimeException("Open API 응답 body가 없습니다.");
        }

        if (response.getResponse().getBody().getItems() == null) {
            throw new RuntimeException("Open API 응답 items가 없습니다.");
        }

        if (response.getResponse().getBody().getItems().getItem() == null) {
            throw new RuntimeException("Open API 응답 item이 없습니다.");
        }

        return response;
    }
}
