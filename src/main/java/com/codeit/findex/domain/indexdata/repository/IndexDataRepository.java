package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataRepositoryCustom {

    @Query(value = "SELECT d.* FROM index_data d " + // 즐겨찾기
            "JOIN index_info i ON d.index_id = i.id " +
            "WHERE i.favorite = true " +
            "AND d.base_date = (SELECT MAX(base_date) FROM index_data WHERE index_id = i.id)",
            nativeQuery = true)
    List<IndexData> findLatestFavoriteIndexData();

}
