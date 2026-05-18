package com.codeit.findex.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// API 응답 형식 통일 클래스
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private T data;

    private String message;

    // 기본 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                data,
                "요청이 성공했습니다."
        );
    }

    // 메시지 포함 성공 응답
    public static <T> ApiResponse<T> success(
            T data,
            String message
    ) {
        return new ApiResponse<>(
                true,
                data,
                message
        );
    }

}