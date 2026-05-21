package com.codeit.findex.infra.openapi.service;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.infra.openapi.dto.OpenApiIndexItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OpenApiIndexDataService {

    private static final DateTimeFormatter BASE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final IndexDataService indexDataService;

    @Transactional
    public void save(IndexInfoResponse indexInfo, OpenApiIndexItemDto item) {

        LocalDate parsedDate = LocalDate.parse(
                item.getBasDt(),
                BASE_DATE_FORMAT
        );

        if (indexDataService.existsByIndexInfoIdAndBaseDate(indexInfo.id(), parsedDate)) {
            return;
        }

        IndexDataCreateRequest request = new IndexDataCreateRequest(
                indexInfo.id(),
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

        indexDataService.create(request, SourceType.OPEN_API);
    }
}