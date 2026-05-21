package com.codeit.findex.domain.autosync.scheduler;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.repository.AutoSyncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

    private final AutoSyncRepository autoSyncRepository;
    // TODO: 외부 API 호출 서비스나 데이터 저장 서비스 여기에 주입


    @Scheduled(cron = "${scheduler.auto-sync.cron}")
    public void runAutoSyncBatch() {
        log.info(" [자동 연동 스케줄러] 매일 자정 배치 작업을 시작합니다...");

        // 스위치가 켜진(enabled = true) 지수들 수집
        List<AutoSync> activeSyncs = autoSyncRepository.findByEnabledTrue();

        if (activeSyncs.isEmpty()) {
            log.info(" [자동 연동 스케줄러] 활성화된 지수가 없습니다. 작업을 종료합니다.");
            return;
        }

        // 2. 켜져 있는 지수들 순회 작업
        for (AutoSync sync : activeSyncs) {
            try {
                // 지수 정보(IndexInfo) 추출
                var indexInfo = sync.getIndexInfo();
                log.info(" 연동 대상 지수: {}", indexInfo.getIndexName());

                // 마지막 연동 날짜 구하기
                // TODO: IndexDataRepository에서 이 지수의 가장 최근 baseDate를 조회하는 로직 필요

                LocalDate startDate = LocalDate.now().minusDays(1); // 임시 세팅
                LocalDate endDate = LocalDate.now(); // 최신 날짜

                log.info("   - 연동 기간: {} ~ {}", startDate, endDate);

                // 외부 API에서 데이터 가져오기 및 DB 저장
                // TODO: 외부 API 호출 및 IndexData 저장 메서드 실행

            } catch (Exception e) {
                // 하나의 지수가 실패해도 다른 지수들은 계속 연동
                log.error("[자동 연동 실패] 지수명: {}, 원인: {}", sync.getIndexInfo().getIndexName(), e.getMessage());
            }
        }

        log.info(" [자동 연동 스케줄러] 배치 작업이 모두 완료되었습니다!");
    }
}