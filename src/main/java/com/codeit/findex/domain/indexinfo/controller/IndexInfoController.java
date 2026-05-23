package com.codeit.findex.domain.indexinfo.controller;

import com.codeit.findex.domain.indexinfo.controller.api.IndexInfoApi;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoSummaryDto;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import com.codeit.findex.global.common.dto.CursorPageResponse;
import com.codeit.findex.infra.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController implements IndexInfoApi {

    // 지수 정보 Service
    private final IndexInfoService indexInfoService;
    private final OpenApiService openApiService;

    // 지수 정보 등록 API
    @PostMapping
    public ResponseEntity<IndexInfoResponse> create(
            @RequestBody IndexInfoCreateRequest request
    ) {

        return ResponseEntity.ok(
                indexInfoService.create(request)
        );
    }

    // 지수 정보 목록 조회 API
    @GetMapping
    public ResponseEntity<CursorPageResponse<IndexInfoResponse>> findAll(
            @RequestParam(required = false) String indexClassification,
            @RequestParam(required = false) String indexName,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false, defaultValue = "indexClassification") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(
                indexInfoService.findAll(indexClassification, indexName, favorite, sortField, sortDirection)
        );
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<IndexInfoSummaryDto>> getSummaries() {
        return ResponseEntity.ok(indexInfoService.getSummaries());
    }


    // 지수 정보 단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoResponse> findById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                indexInfoService.findById(id)
        );
    }

    // 지수 정보 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoResponse> update(
            @PathVariable String id,
            @RequestBody IndexInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.update(id, request)
        );
    }

    // 지수 정보 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id
    ) {
        indexInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
