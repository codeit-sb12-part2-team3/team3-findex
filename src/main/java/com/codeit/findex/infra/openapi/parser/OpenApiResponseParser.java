package com.codeit.findex.infra.openapi.parser;

import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenApiResponseParser {

    public List<OpenApiIndexItemDto> parseItems(OpenApiResponseDto response) {

        if (response == null || response.getResponse() == null) {
            throw new RuntimeException("파싱할 Open API 응답이 없습니다.");
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

        return response.getResponse()
                .getBody()
                .getItems()
                .getItem();
    }
}