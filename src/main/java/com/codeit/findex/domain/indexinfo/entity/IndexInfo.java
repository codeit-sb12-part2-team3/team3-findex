package com.codeit.findex.domain.indexinfo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "index_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "index_name", length = 100)
    private String indexName;

    @Column(name = "index_classification", length = 50)
    private String indexClassification;

    @Column(name = "employed_items_count")
    private Integer employedItemsCount;

    @Column(name = "base_point_in_time")
    private LocalDate basePointInTime;

    @Column(name = "base_index", precision = 20, scale = 2)
    private BigDecimal baseIndex;

    @Column(name = "source_type", length = 20)
    private String sourceType;

    private Boolean favorite;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
