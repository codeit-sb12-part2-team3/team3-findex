package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID> {

    Optional<IndexInfo> findByIndexName(String indexName);

    List<IndexInfo> findByIndexNameIn(List<String> indexNames);

    @Query("""
        SELECT i FROM IndexInfo i
        WHERE (:indexClassification IS NULL
            OR :indexClassification = ''
            OR i.indexClassification LIKE CONCAT('%', :indexClassification, '%'))
        AND (:indexName IS NULL
            OR :indexName = ''
            OR i.indexName LIKE CONCAT('%', :indexName, '%'))
        AND (:favorite IS NULL
            OR i.favorite = :favorite)
        ORDER BY i.indexClassification ASC
    """)
    List<IndexInfo> search(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite,
            Pageable pageable
    );

    // AutoSync 레코드가 없는 IndexInfo만 반환 (누락된 AutoSync 동기화용)
    @Query("""
        SELECT i FROM IndexInfo i 
                WHERE i.id 
                        NOT IN (SELECT a.indexInfo.id FROM AutoSync a)
    """)
    List<IndexInfo> findIndexInfoWithoutAutoSync();
}