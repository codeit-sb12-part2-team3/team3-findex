package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
