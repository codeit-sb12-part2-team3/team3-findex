package com.codeit.findex.domain.dashboard.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<IndexData, UUID> {

    @Query("SELECT d FROM IndexData d JOIN FETCH d.indexInfo i " +
            "WHERE d.baseDate = (SELECT MAX(sub.baseDate) FROM IndexData sub WHERE sub.indexInfoId = d.indexInfoId)")
    List<IndexData> findLatestIndexDataWithInfo();
}