package com.codeit.findex.domain.autosync.scheduler;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import com.codeit.findex.domain.syncjob.repository.SyncJobRepository;
import com.codeit.findex.infra.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.auto-sync.enabled", havingValue = "true", matchIfMissing = true)
public class AutoSyncScheduler {

    private static final String INDEX_DATA_JOB = "INDEX_DATA";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";
    private static final String SCHEDULER_WORKER = "관리자";
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");

    private final AutoSyncRepository autoSyncRepository;
    private final IndexDataRepository indexDataRepository;
    private final OpenApiService openApiService;
    private final SyncJobRepository syncJobRepository;

    @Scheduled(cron = "${scheduler.auto-sync.cron}", zone = "Asia/Seoul")
    public void runAutoSyncBatch() {
        log.info("[자동 연동 스케줄러] 매일 자정 배치 작업을 시작합니다...");

        List<AutoSync> activeSyncs = autoSyncRepository.findByEnabledTrue();

        if (activeSyncs.isEmpty()) {
            log.info("[자동 연동 스케줄러] 활성화된 지수가 없습니다. 작업을 종료합니다.");
            return;
        }

        for (AutoSync sync : activeSyncs) {
            IndexInfo indexInfo = sync.getIndexInfo();
            log.info("[자동 연동] 연동 대상 지수: {}", indexInfo.getIndexName());

            // 해당 지수의 가장 최근 baseDate 조회
            LocalDate latestDate = indexDataRepository.findMaxBaseDateByIndexInfoId(indexInfo.getId());

            // 장 마감(15:30) 후 데이터가 확정되므로 전일까지만 연동 (KST 기준)
            LocalDate endDate = LocalDate.now(BUSINESS_ZONE).minusDays(1);

            LocalDate startDate = (latestDate != null)
                    ? latestDate.plusDays(1)
                    : endDate.minusDays(6);

            if (startDate.isAfter(endDate)) {
                log.info("[자동 연동] {}는 이미 최신 데이터 보유. 건너뜁니다.", indexInfo.getIndexName());
                continue;
            }

            log.info("[자동 연동] {} 연동 기간: {} ~ {}", indexInfo.getIndexName(), startDate, endDate);

            LocalDate targetDate = startDate;
            while (!targetDate.isAfter(endDate)) {
                if (indexDataRepository.existsByIndexInfoIdAndBaseDate(indexInfo.getId(), targetDate)) {
                    log.info("[자동 연동] {} {} 데이터 이미 존재. 건너뜁니다.", indexInfo.getIndexName(), targetDate);
                    targetDate = targetDate.plusDays(1);
                    continue;
                }

                String result = SUCCESS;
                try {
                    String baseDateStr = targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);
                    openApiService.syncAndSaveIndexData(indexInfo.getIndexName(), baseDateStr, 1, 100);
                } catch (Exception e) {
                    result = FAILED;
                    log.error("[자동 연동 실패] 지수명: {}, 날짜: {}, 원인: {}",
                            indexInfo.getIndexName(), targetDate, e.getMessage());
                }

                syncJobRepository.save(SyncJob.builder()
                        .indexInfo(indexInfo)
                        .jobType(INDEX_DATA_JOB)
                        .targetDate(targetDate)
                        .worker(SCHEDULER_WORKER)
                        .jobTime(LocalDateTime.now(BUSINESS_ZONE))
                        .result(result)
                        .build());

                targetDate = targetDate.plusDays(1);
            }
        }

        log.info("[자동 연동 스케줄러] 배치 작업이 모두 완료되었습니다!");
    }
}
