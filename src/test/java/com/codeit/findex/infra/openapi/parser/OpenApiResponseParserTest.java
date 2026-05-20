package com.codeit.findex.infra.openapi.parser;

import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenApiResponseParserTest {

    private final OpenApiResponseParser parser =
            new OpenApiResponseParser();

    @Test
    void response가_null이면_예외_발생() {

        assertThatThrownBy(() -> parser.parseItems(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.OPEN_API_PARSE_FAILED.getMessage());
    }
}