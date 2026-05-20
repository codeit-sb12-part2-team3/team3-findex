package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.*;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController {

    private final IndexDataService indexDataService;

    @PostMapping
    public ResponseEntity<IndexDataResponse> create(
            @RequestBody IndexDataCreateRequest request) {
        IndexDataResponse created = indexDataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexDataResponse> update(
            @PathVariable UUID id,
            @RequestBody IndexDataUpdateRequest request) {
        IndexDataResponse updated = indexDataService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        indexDataService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<IndexDataSearchResponse<IndexDataResponse>> searchIndexData(
            @RequestBody IndexDataSearchRequest searchRequest) {
        IndexDataSearchResponse<IndexDataResponse> result = indexDataService.getIndexDataList(searchRequest);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
