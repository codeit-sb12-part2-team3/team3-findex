package com.codeit.findex.domain.autosync.controller;

import com.codeit.findex.domain.autosync.controller.api.AutoSyncApi;
import com.codeit.findex.domain.autosync.dto.AutoSyncConfigDto;
import com.codeit.findex.domain.autosync.dto.AutoSyncConfigUpdateRequest;
import com.codeit.findex.domain.autosync.dto.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.domain.autosync.service.AutoSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auto-sync-configs")
@RequiredArgsConstructor
public class AutoSyncController implements AutoSyncApi {

    private final AutoSyncService autoSyncService;

    /*
     * 1. 자동 연동 설정 목록 조회
     * [GET] /api/auto-sync-configs?nextIdAfter={uuid}&indexId={uuid}&enabled={boolean}&sort={string}&size={int}
     */
    @GetMapping
    public ResponseEntity<CursorPageResponseAutoSyncConfigDto> getAutoSyncConfigs(
            @RequestParam(name = "idAfter", required = false) UUID nextIdAfter,
            @RequestParam(name = "indexInfoId", required = false) UUID indexId,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "10") int size) {

        CursorPageResponseAutoSyncConfigDto response =
                autoSyncService.getAutoSyncConfigs(nextIdAfter, indexId, enabled, sort, size);

        // HTTP 200 OK
        return ResponseEntity.ok(response);
    }

    /*
     * 2. 자동 연동 설정 수정 (활성화 여부 토글)
     * [PATCH] /api/auto-sync-configs/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigDto> updateAutoSyncConfig(
            @PathVariable UUID id,
            @Valid @RequestBody AutoSyncConfigUpdateRequest request) {

        AutoSyncConfigDto response = autoSyncService.updateAutoSyncStatus(id, request);

        // HTTP 200 OK
        return ResponseEntity.ok(response);
    }
}