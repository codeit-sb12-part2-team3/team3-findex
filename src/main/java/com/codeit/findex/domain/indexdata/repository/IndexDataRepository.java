package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.dto.IndexDataDto;
import com.codeit.findex.domain.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.domain.Limit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataRepositoryCustom {

    List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByFluctuationRateDesc(UUID indexInfoId, LocalDate baseDateAfter, LocalDate baseDateBefore, Limit limit);
}
