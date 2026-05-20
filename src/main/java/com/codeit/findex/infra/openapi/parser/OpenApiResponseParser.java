package com.codeit.findex.infra.openapi.parser;

import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenApiResponseParser {

    public List<OpenApiIndexItemDto> parseItems(OpenApiResponseDto response) {

        if (response == null || response.getResponse() == null) {
            throw new BusinessException(ErrorCode.OPEN_API_PARSE_FAILED);
        }

        if (response.getResponse().getBody() == null) {
            throw new BusinessException(ErrorCode.OPEN_API_PARSE_FAILED);

        }

        if (response.getResponse().getBody().getItems() == null) {
            throw new BusinessException(ErrorCode.OPEN_API_PARSE_FAILED);
        }

        if (response.getResponse().getBody().getItems().getItem() == null) {
            throw new BusinessException(ErrorCode.OPEN_API_PARSE_FAILED);
        }

        return response.getResponse()
                .getBody()
                .getItems()
                .getItem();
    }
}