package com.codeit.findex.domain.indexinfo.controller;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    // 지수 정보 Service
    private final IndexInfoService indexInfoService;

    // 지수 정보 등록 API
    @PostMapping
    public ResponseEntity<IndexInfoResponse> create(
            @RequestBody IndexInfoCreateRequest request
    ) {

        return ResponseEntity.ok(
                indexInfoService.create(request)
        );
    }
}