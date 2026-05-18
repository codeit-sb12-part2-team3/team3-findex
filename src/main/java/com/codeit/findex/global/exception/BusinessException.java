package com.codeit.findex.global.exception;

import lombok.Getter;

@Getter
// 공통 비즈니스 예외 클래스
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {

        super(errorCode.getMessage());

        this.errorCode = errorCode;
    }

}