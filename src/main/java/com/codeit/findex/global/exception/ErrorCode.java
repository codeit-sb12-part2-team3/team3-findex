package com.codeit.findex.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
// 공통 에러 코드 관리
public enum ErrorCode {

    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다."
    ),

    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "요청한 데이터를 찾을 수 없습니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다."
    ),

    // Open API 관련 에러
    OPEN_API_NO_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "Open API 응답이 없습니다."
    ),

    OPEN_API_INVALID_RESPONSE(
            HttpStatus.BAD_GATEWAY,
            "Open API 응답이 유효하지 않습니다."
    ),

    OPEN_API_REQUEST_FAILED(
            HttpStatus.BAD_GATEWAY,
            "Open API 요청에 실패했습니다."
    ),

    OPEN_API_PARSE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Open API 응답 파싱에 실패했습니다."
    );

    private final HttpStatus status;

    private final String message;

}