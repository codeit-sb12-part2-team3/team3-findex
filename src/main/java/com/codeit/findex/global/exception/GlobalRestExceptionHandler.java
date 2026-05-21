package com.codeit.findex.global.exception;

import org.springframework.http.ResponseEntity;


import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgument(IllegalArgumentException e) {
        System.out.println("[BAD REQUEST ERROR] 잘못된 매개변수 요청: " + e.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> HttpMessageNotReadable(HttpMessageNotReadableException e) {
        System.out.println("[MESSAGE NOT READABLE] 요청 본문 파싱 실패: " + e.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        System.out.println("[MISSING PARAMETER] 필수 요청 파라미터 누락: " + e.getParameterName());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> NoSuchElement(NoSuchElementException e) {
        System.out.println("[NOT FOUND ERROR] 요청한 데이터를 찾을 수 없음: " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler({SocketTimeoutException.class, java.net.ConnectException.class})
    public ResponseEntity<ErrorResponse> Timeout(Exception e) {
        System.out.println("[TIMEOUT ERROR] 서버 응답 지연 발생: " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ErrorResponse.of(ErrorCode.GATEWAY_TIMEOUT));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> HttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        System.out.println("[METHOD NOT ALLOWED] 지원하지 않는 HTTP 메소드: " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        System.out.println("[UNSUPPORTED MEDIA TYPE] 지원하지 않는 Content-Type: " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> Business(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        System.out.println("[BUSINESS] API 문제 발생: " + e.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> etc(Exception e, WebRequest request) {
        System.out.println("[Exception] 예기치 못한 오류: " + e.getMessage());
        System.out.println("Request URI: " + request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

}
