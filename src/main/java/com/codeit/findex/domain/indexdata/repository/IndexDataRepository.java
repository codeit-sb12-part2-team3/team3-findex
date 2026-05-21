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

    @Query("SELECT d FROM IndexData d JOIN FETCH d.indexInfo i " +
            "WHERE d.baseDate BETWEEN :startDate AND :endDate " +
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
}