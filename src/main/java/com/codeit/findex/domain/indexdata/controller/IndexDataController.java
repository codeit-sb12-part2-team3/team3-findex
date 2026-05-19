package com.codeit.findex.domain.indexdata.controller;

import com.codeit.findex.domain.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.domain.indexdata.dto.IndexDataResponse;
import com.codeit.findex.domain.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
