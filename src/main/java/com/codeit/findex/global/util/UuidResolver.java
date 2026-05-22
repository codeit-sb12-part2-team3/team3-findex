package com.codeit.findex.global.util;

import com.codeit.findex.domain.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UuidResolver {

    private final IndexInfoService indexInfoService;

    /**
     * UUID 문자열 또는 numeric ID(IndexInfoSummaryDto.id)를 UUID로 변환.
     * 프론트에서 Number(key)를 사용하지 않는 경우 UUID 문자열이 오고,
     * Number(key) 변환 없이 as number 캐스팅만 한 경우에도 UUID 문자열이 온다.
     * 숫자 문자열(numeric hash)이 오는 경우 indexInfoService를 통해 UUID 역조회.
     */
    public UUID resolve(String id) {
        if (id == null || id.isBlank()) return null;
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ignored) {}
        try {
            int numericId = Integer.parseInt(id);
            return indexInfoService.getUuidByNumericId(numericId);
        } catch (NumberFormatException ignored) {}
        return null;
    }
}
