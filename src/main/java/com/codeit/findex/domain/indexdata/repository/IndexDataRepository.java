package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.entity.IndexData;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {
}
