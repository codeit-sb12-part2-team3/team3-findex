package com.codeit.findex.infra.openapi;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import com.codeit.findex.global.exception.BusinessException;
import com.codeit.findex.global.exception.ErrorCode;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import com.codeit.findex.infra.openapi.dto.OpenApiResponseDto;
import com.codeit.findex.infra.openapi.parser.OpenApiResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final OpenApiClient openApiClient;
    private final OpenApiResponseParser openApiResponseParser;
    private final IndexInfoService indexInfoService;
    private final IndexDataService indexDataService;
    private final IndexInfoRepository indexInfoRepository;

    // 전체 지수 정보 저장 (DB 비어있을 때)
    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveAllIndexInfo() {
        OpenApiResponseDto response = openApiClient.getStockMarketIndex(
                null, null, 1, 100);

        List<OpenApiIndexItemDto> items =
                openApiResponseParser.parseItems(response);

        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.OPEN_API_INVALID_RESPONSE);
        }

        for (OpenApiIndexItemDto item : items) {
            Optional<IndexInfo> existing = indexInfoRepository.findByIndexName(item.getIdxNm());
            if (existing.isPresent()) {
                existing.get().update(
                        item.getEpyItmsCnt() != null
                                ? Integer.parseInt(item.getEpyItmsCnt())
                                : null,
                        null,
                        item.getBasIdx() != null
                                ? new BigDecimal(item.getBasIdx())
                                : null,
                        existing.get().getFavorite()
                );
                continue;
            }

            IndexInfoCreateRequest indexInfoRequest =
                    new IndexInfoCreateRequest(
                            item.getIdxNm(),
                            item.getIdxCsf(),
                            item.getEpyItmsCnt() != null
                                    ? Integer.parseInt(item.getEpyItmsCnt())
                                    : null,
                            null,
                            item.getBasIdx() != null
                                    ? new BigDecimal(item.getBasIdx())
                                    : null,
                            false
                    );
            indexInfoService.create(indexInfoRequest);
        }

        return items;
    }

    // 특정 지수 정보 저장
    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveIndexInfo(String indexName) {
        OpenApiResponseDto response = openApiClient.getStockMarketIndex(
                indexName, null, 1, 100);

        List<OpenApiIndexItemDto> items =
                openApiResponseParser.parseItems(response);

        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.OPEN_API_INVALID_RESPONSE);
        }

        for (OpenApiIndexItemDto item : items) {
            Optional<IndexInfo> existing = indexInfoRepository.findByIndexName(item.getIdxNm());
            if (existing.isPresent()) {
                existing.get().update(
                        item.getEpyItmsCnt() != null
                                ? Integer.parseInt(item.getEpyItmsCnt())
                                : null,
                        null,
                        item.getBasIdx() != null
                                ? new BigDecimal(item.getBasIdx())
                                : null,
                        existing.get().getFavorite()
                );
                continue;
            }

            IndexInfoCreateRequest indexInfoRequest =
                    new IndexInfoCreateRequest(
                            item.getIdxNm(),
                            item.getIdxCsf(),
                            item.getEpyItmsCnt() != null
                                    ? Integer.parseInt(item.getEpyItmsCnt())
                                    : null,
                            null,
                            item.getBasIdx() != null
                                    ? new BigDecimal(item.getBasIdx())
                                    : null,
                            false
                    );
            indexInfoService.create(indexInfoRequest);
        }

        return items;
    }

    // 지수 데이터 저장
    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveIndexData(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        OpenApiResponseDto response = openApiClient.getStockMarketIndex(
                indexName, baseDate, pageNo, numOfRows);

        List<OpenApiIndexItemDto> items =
                openApiResponseParser.parseItems(response);

        items = items.stream()
                .filter(item -> item.getIdxNm() != null
                        && item.getIdxNm().contains(indexName))
                .toList();

        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.OPEN_API_INVALID_RESPONSE);
        }

        for (OpenApiIndexItemDto item : items) {
            IndexInfoCreateRequest indexInfoRequest =
                    new IndexInfoCreateRequest(
                            item.getIdxNm(),
                            item.getIdxCsf(),
                            item.getEpyItmsCnt() != null
                                    ? Integer.parseInt(item.getEpyItmsCnt())
                                    : null,
                            null,
                            item.getBasIdx() != null
                                    ? new BigDecimal(item.getBasIdx())
                                    : null,
                            false
                    );

            IndexInfoResponse savedInfo =
                    indexInfoService.create(indexInfoRequest);

            LocalDate parsedDate = LocalDate.parse(
                    item.getBasDt(),
                    DateTimeFormatter.ofPattern("yyyyMMdd")
            );

            IndexDataCreateRequest indexDataCreateRequest =
                    new IndexDataCreateRequest(
                            savedInfo.id(),
                            parsedDate,
                            new BigDecimal(item.getMkp()),
                            new BigDecimal(item.getClpr()),
                            new BigDecimal(item.getHipr()),
                            new BigDecimal(item.getLopr()),
                            new BigDecimal(item.getVs()),
                            new BigDecimal(item.getFltRt()),
                            Long.parseLong(item.getTrqu()),
                            Long.parseLong(item.getTrPrc()),
                            Long.parseLong(item.getLstgMrktTotAmt())
                    );

            try {
                indexDataService.create(indexDataCreateRequest, SourceType.OPEN_API);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ErrorCode.OPEN_API_SAVE_FAILED);
            }
        }

        return items;
    }
}