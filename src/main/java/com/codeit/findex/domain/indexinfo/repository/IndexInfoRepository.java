package com.codeit.findex.domain.indexinfo.repository;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID> {


}