package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.*;
import com.codeit.findex.domain.indexdata.entity.PeriodType;
import com.codeit.findex.domain.indexdata.entity.SourceType;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Period;
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
            @RequestBody IndexDataSearchRequest searchRequest) {
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
}
