package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataRepositoryCustom {

}
