package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

    Optional<IndexData> findByIndexId(UUID indexId);

    List<IndexData> findByIndexIdAndBaseDateBetween (UUID indexId, Date startDate, Date endDate);

    List<IndexData> findByIndexIdAndBaseDateBetweenOrderByBaseDateAsc(UUID indexId, Date startDate, Date endDate, Pageable pageable);

    List<IndexData> findByIndexIdAndBaseDateBetweenOrderByBaseDateDesc(UUID indexId, Date startDate, Date endDate, Pageable pageable);

    List<IndexData> findByIndexIdInAndBaseDateBetween(List<UUID> indexIds, Date startDate, Date endDate, Pageable pageable);

    @Query(value = "SELECT d.* FROM index_data d " + // 즐겨찾기
            "JOIN index_info i ON d.index_id = i.id " +
            "WHERE i.favorite = true " +
            "AND d.base_date = (SELECT MAX(base_date) FROM index_data WHERE index_id = i.id)",
            nativeQuery = true)
    List<IndexData> findLatestFavoriteIndexData();

}
