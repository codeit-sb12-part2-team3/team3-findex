package com.codeit.findex.infra.openapi;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
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

@Service
@RequiredArgsConstructor

public class OpenApiService {

    private final OpenApiClient openApiClient;
    private final OpenApiResponseParser openApiResponseParser;
    private final IndexInfoService indexInfoService;
    private final IndexDataService indexDataService;

    @Transactional
    public List<OpenApiIndexItemDto> syncAndSaveIndexData(
            String indexName,
            String baseDate,
            Integer pageNo,
            Integer numOfRows
    ) {
        OpenApiResponseDto response = openApiClient.getStockMarketIndex(
                indexName,
                baseDate,
                pageNo,
                numOfRows
        );

        List<OpenApiIndexItemDto> items =
                openApiResponseParser.parseItems(response);

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
                            "OPEN_API",
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

            indexDataService.create(indexDataCreateRequest,SourceType.OPEN_API);
        }

        return items;

    }
}
