package com.codeit.findex.infra.openapi.service;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
                        null,
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
                null,
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
}