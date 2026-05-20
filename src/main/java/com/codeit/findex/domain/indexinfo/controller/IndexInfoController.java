package com.codeit.findex.domain.indexinfo.controller;

import com.codeit.findex.domain.indexinfo.dto.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoResponse;
import com.codeit.findex.domain.indexinfo.dto.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import com.codeit.findex.global.common.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    // 지수 정보 목록 조회 API
    @GetMapping
    public ResponseEntity<CursorPageResponse<IndexInfoResponse>> findAll() {
        return ResponseEntity.ok(
                indexInfoService.findAll()
        );
    }

    @GetMapping("/summaries")

    public ResponseEntity<List<IndexInfoResponse>> getSummaries() {
        return ResponseEntity.ok(indexInfoService.getSummaries());
    }

    // 지수 정보 단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                indexInfoService.findById(id)
        );
    }

    // 지수 정보 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoResponse> update(
            @PathVariable UUID id,
            @RequestBody IndexInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.update(id, request)
        );
    }

    // 지수 정보 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        indexInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
