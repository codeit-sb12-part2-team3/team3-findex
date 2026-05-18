package com.codeit.findex.infra.openapi;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OpenApiResponseDto {

    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        private int numOfRows;
        private int pageNo;
        private int totalCount;
        private Items items;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    public static class Item {
        private String basDt; // 기준일자
        private String idxNm; // 지수명
        private String idxCsf; // 지수분류
        private String epyItmsCnt; // 채용종목수
        private String clpr; // 종가
        private String vs; // 전일대비
        private String fltRt; // 등락률
        private String mkp; // 시가
        private String hipr; // 고가
        private String lopr; // 저가
        private String trqu; // 거래량
        private String trPrc; // 거래대금
        private String lstgMrktTotAmt; // 상장시가총액
        private String basPntm; // 기준시점
        private String basIdx; // 기준지수


    }
}