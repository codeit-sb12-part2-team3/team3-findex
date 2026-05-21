package com.codeit.findex.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// 에러 응답 형식 클래스
public class ErrorResponse {

    private String code;

    private String message;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode,String message) {
        return new ErrorResponse(errorCode.name(), message);
    }
}