package com.codeit.findex.domain.indexinfo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "index_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class IndexInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "index_name", nullable = false, length = 100)
    private String indexName;

    @Column(name = "index_classification", nullable = false, length = 50)
    private String indexClassification;

    @Column(name = "employed_items_count")
    private Integer employedItemsCount;

    @Column(name = "base_point_in_time")
    private LocalDate basePointInTime;

    @Column(name = "base_index", precision = 20, scale = 2)
    private BigDecimal baseIndex;

    @Column(name = "source_type", nullable = false, length = 20)
    private String sourceType;

    @Column(nullable = false)
    private Boolean favorite;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public void update(String indexClassification, Integer employedItemsCount) {
        this.indexClassification = indexClassification;
        this.employedItemsCount = employedItemsCount;
        }

    public void update(

            Integer employedItemsCount,
            LocalDate basePointInTime,
            BigDecimal baseIndex,
            Boolean favorite
    ) {
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.favorite = favorite;
    }
}