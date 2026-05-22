package com.codeit.findex.global.init;

import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.codeit.findex.infra.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final OpenApiService openApiService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeOnStartup() {
        log.info("[초기화] 데이터 존재 여부 확인...");

        if (indexInfoRepository.count() == 0) {
            log.info("[초기화] 지수 정보 없음 → Open API 연동 시작");
            try {
                openApiService.syncAndSaveAllIndexInfo();
                log.info("[초기화] 지수 정보 연동 완료");
            } catch (Exception e) {
                log.error("[초기화] 지수 정보 연동 실패: {}", e.getMessage());
            }
        }

        LocalDate latestDate = indexDataRepository.findGlobalMaxBaseDate();
        if (latestDate == null) {
            log.info("[초기화] 지수 데이터 없음 → 최근 7일 데이터 연동 시작");
            LocalDate end = LocalDate.now().minusDays(1);
            LocalDate start = end.minusDays(6);

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                String baseDateStr = date.format(DateTimeFormatter.BASIC_ISO_DATE);
                try {
                    openApiService.syncAndSaveIndexData(null, baseDateStr, 1, 100);
                    log.info("[초기화] {} 연동 완료", date);
                } catch (Exception e) {
                    log.warn("[초기화] {} 연동 실패: {}", date, e.getMessage());
                }
            }
            log.info("[초기화] 초기 데이터 연동 완료");
        } else {
            log.info("[초기화] 기존 데이터 존재 (최신: {}), 초기 연동 스킵", latestDate);
        }
    }
}
