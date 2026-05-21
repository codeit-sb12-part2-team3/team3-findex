package com.codeit.findex.domain.autosync.scheduler;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;
import com.codeit.findex.domain.indexdata.repository.IndexDataRepository;
import com.codeit.findex.domain.syncjob.service.SyncJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private final AutoSyncRepository autoSyncRepository;
    private final IndexDataRepository indexDataRepository;
    private final SyncJobService syncJobService;


    @Scheduled(cron = "${scheduler.auto-sync.cron}")
    public void runAutoSyncBatch() {
        log.info(" [자동 연동 스케줄러] 매일 자정 배치 작업을 시작합니다...");

        // 스위치가 켜진(enabled = true) 지수들 수집
        List<AutoSync> activeSyncs = autoSyncRepository.findByEnabledTrueWithIndexInfo();

        if (activeSyncs.isEmpty()) {
            log.info(" [자동 연동 스케줄러] 활성화된 지수가 없습니다. 작업을 종료합니다.");
            return;
        }

        // 지수 순회 작업
        for (AutoSync sync : activeSyncs) {
            // indexInfo 접근을 try 바깥에서 한 번만: JOIN FETCH로 이미 로딩된 상태
            UUID indexInfoId = sync.getIndexInfo().getId();
            String indexName = sync.getIndexInfo().getIndexName();
            try {
                log.info("🚀 연동 대상 지수: {}", indexName);

                // 이 지수의 마지막 데이터 날짜 조회
                LocalDate latestDate = indexDataRepository.findMaxBaseDateByIndexInfoId(indexInfoId);

                LocalDate startDate;
                if (latestDate != null) {
                    startDate = latestDate.plusDays(1); // 마지막 데이터의 다음 날부터 가져옴
                } else {
                    startDate = LocalDate.now().minusDays(7); // 만약 데이터가 아예 없으면 최근 7일치 가져옴
                }

                LocalDate endDate = LocalDate.now(); // 오늘까지 가져옴

                // 이미 오늘 날짜까지 최신화되어 있다면 넘어감
                if (startDate.isAfter(endDate)) {
                    log.info("   - 🟢 이미 최신 데이터가 존재합니다. 다음 지수로 넘어갑니다.");
                    continue;
                }

                log.info("   - 📅 연동 기간: {} ~ {}", startDate, endDate);

                // 연동 서비스
                // workerIp 자리에 "SYSTEM_SCHEDULER"라고 적어서 프론트 '연동 이력' 표에 시스템이 했다고 표시
                syncJobService.syncIndexData(
                        List.of(indexInfoId.toString()), // 리스트 형태로 감싸서 전달
                        startDate,
                        endDate,
                        "SYSTEM_SCHEDULER"
                );

                log.info("   - 🔵 {} 연동 성공!", indexName);

            } catch (Exception e) {
                log.error("❌ [자동 연동 실패] 지수명: {}, 원인: {}", indexName, e.getMessage());
            }
        }

        log.info("✅ [자동 연동 스케줄러] 배치 작업이 모두 완료되었습니다!");
    }
}