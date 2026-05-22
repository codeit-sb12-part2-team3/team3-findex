package com.codeit.findex.infra.openapi.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenApiIndexInfoService {

    private final IndexInfoService indexInfoService;

    @Transactional
    public List<IndexInfoResponse> saveAll(List<OpenApiIndexItemDto> items) {
        List<IndexInfoCreateRequest> requests = items.stream()
                .map(item -> new IndexInfoCreateRequest(
                        item.getIdxNm(),
                        item.getIdxCsf(),
                        toInteger(item.getEpyItmsCnt()),
                        parseBasPntm(item.getBasPntm()),
                        toBigDecimal(item.getBasIdx()),
                        false
                ))
                .toList();

        return indexInfoService.syncIndexInfos(requests);
    }

    public IndexInfoResponse saveOrUpdate(OpenApiIndexItemDto item) {
        IndexInfoCreateRequest request = new IndexInfoCreateRequest(
                item.getIdxNm(),
                item.getIdxCsf(),
                toInteger(item.getEpyItmsCnt()),
                parseBasPntm(item.getBasPntm()),
                toBigDecimal(item.getBasIdx()),
                false
        );
        return indexInfoService.syncIndexInfo(request);
    }

    private Integer toInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    private BigDecimal toBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    // basPntm 다양한 형식 파싱
    private LocalDate parseBasPntm(String basPntm) {
        if (basPntm == null || basPntm.isBlank()) return null;
        String trimmed = basPntm.trim();

        // YYYYMMDD
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (Exception ignored) { }

        // YYYY.MM.DD
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } catch (Exception ignored) { }

        // YYYY년 M월 D일
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyy년 M월 d일"));
        } catch (Exception ignored) { }

        // YYYY년MM월DD일
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyy년MM월dd일"));
        } catch (Exception ignored) { }

        // 연도만 ("YYYY")
        try {
            int year = Integer.parseInt(trimmed);
            if (year >= 1900 && year <= 2100) {
                return LocalDate.of(year, 1, 1);
            }
        } catch (Exception ignored) { }

        return null;
    }
}