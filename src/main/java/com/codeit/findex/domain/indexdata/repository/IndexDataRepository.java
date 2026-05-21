package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataRepositoryCustom {

    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByFluctuationRateDesc(UUID indexInfoId, LocalDate baseDateAfter, LocalDate baseDateBefore, Limit limit);

    boolean existsByIndexInfoIdAndBaseDate(UUID indexInfoId, LocalDate baseDate);

    List<IndexData> findByIndexInfoIdInAndBaseDateIn(
            List<UUID> indexInfoIds,
            List<LocalDate> baseDates
    );

    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
            UUID indexInfoId, LocalDate startDate, LocalDate endDate
    );

    // 기간 내 지수별 최신 데이터 1건씩 추출 후 등락률 기준 상위 N개 반환
    // 서브쿼리로 각 지수의 MAX(baseDate)만 선택해 중복 지수 등장을 방지
    @Query("SELECT d FROM IndexData d JOIN FETCH d.indexInfo " +
            "WHERE d.baseDate BETWEEN :startDate AND :endDate " +
            "AND d.baseDate = (" +
            "  SELECT MAX(sub.baseDate) FROM IndexData sub " +
            "  WHERE sub.indexInfo.id = d.indexInfo.id " +
            "  AND sub.baseDate BETWEEN :startDate AND :endDate" +
            ") " +
            "ORDER BY d.fluctuationRate DESC")
    List<IndexData> findTopRankedIndexData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT d FROM IndexData d JOIN FETCH d.indexInfo i " +
            "WHERE i.favorite = true " +
            "AND d.baseDate = (SELECT MAX(sub.baseDate) FROM IndexData sub WHERE sub.indexInfo.id = d.indexInfo.id)")
    List<IndexData> findLatestFavoriteIndexData();

    // 스케줄러용: 특정 지수의 가장 최신 기준일 조회
    @Query("SELECT MAX(d.baseDate) FROM IndexData d WHERE d.indexInfo.id = :indexInfoId")
    LocalDate findMaxBaseDateByIndexInfoId(@Param("indexInfoId") UUID indexInfoId);
}