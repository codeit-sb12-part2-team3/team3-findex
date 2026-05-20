package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.*;
import com.codeit.findex.domain.indexdata.entity.PeriodType;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController {

    private final IndexDataService indexDataService;

    @PostMapping
    public ResponseEntity<IndexDataDto> create(
            @RequestBody IndexDataCreateRequest request) {
        IndexDataDto created = indexDataService.create(request, SourceType.USER);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexDataDto> update(
            @PathVariable UUID id,
            @RequestBody IndexDataUpdateRequest request) {
        IndexDataDto updated = indexDataService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        indexDataService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseIndexDataDto<IndexDataDto>> searchIndexData(
            @ModelAttribute IndexDataSearchRequest searchRequest) {
        CursorPageResponseIndexDataDto<IndexDataDto> result = indexDataService.getIndexDataList(searchRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/performance/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> performanceRank(
            @RequestParam UUID indexInfoId,
            @RequestParam(name = "periodType", defaultValue = "DAILY") String period,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ){
        PeriodType periodType = PeriodType.fromString(period);
        List<RankedIndexPerformanceDto> response = indexDataService.getRank(indexInfoId,periodType,limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<StreamingResponseBody> exportCsv(
            @ModelAttribute IndexDataSearchRequest searchRequest
    ) {
        StreamingResponseBody body = outputStream -> {
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
            )) {
                writer.write('\uFEFF');
                indexDataService.exportCsv(searchRequest, writer);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"index-data.csv\"")
                .body(body);
    }
}
