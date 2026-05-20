package com.codeit.findex.domain.autosync.controller;

import com.codeit.findex.domain.autosync.dto.AutoSyncCreateRequest;
import com.codeit.findex.domain.autosync.dto.AutoSyncResponse;
import com.codeit.findex.domain.autosync.dto.AutoSyncUpdateRequest;
import com.codeit.findex.domain.autosync.service.AutoSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auto-syncs")
@RequiredArgsConstructor
public class AutoSyncController {

    private final AutoSyncService autoSyncService;

    @PostMapping
    public ResponseEntity<AutoSyncResponse> createAutoSync(
            @Valid @RequestBody AutoSyncCreateRequest request) {

        AutoSyncResponse response = autoSyncService.createAutoSync(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{indexId}")
    public ResponseEntity<AutoSyncResponse> getAutoSyncStatus(@PathVariable UUID indexId) {
        AutoSyncResponse response = autoSyncService.getAutoSyncStatus(indexId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{indexId}")
    public ResponseEntity<AutoSyncResponse> updateAutoSyncStatus(
            @PathVariable UUID indexId,
            @Valid @RequestBody AutoSyncUpdateRequest request) {

        AutoSyncResponse response = autoSyncService.updateAutoSyncStatus(indexId, request);
        return ResponseEntity.ok(response);
    }
}