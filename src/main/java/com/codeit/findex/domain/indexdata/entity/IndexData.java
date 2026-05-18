package com.codeit.findex.domain.indexdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "index_data",
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_indexdata_indexid_basedate",
                columnNames = {"index_id","base_date"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class IndexData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "index_id", nullable = false)
    private UUID indexId;

    @Column(name = "base_date",nullable = false,
    columnDefinition = "기준 일자")
    private Date baseDate;

    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "market_price", nullable = false,
    columnDefinition = "시가")
    private Double marketPrice;

    @Column(name = "closing_price", nullable = false,
    columnDefinition = "종가")
    private Double closingPrice;

    @Column(name = "high_price", nullable = false,
    columnDefinition = "고가")
    private Double highPrice;

    @Column(name = "low_price", nullable = false,
            columnDefinition = "저가")
    private Double lowPrice;

    @Column(nullable = false,
            columnDefinition = "대비")
    private Double versus;

    @Column(name = "fluctuation_rate",nullable = false,
            columnDefinition = "등락율")
    private Double fluctuationRate;

    @Column(name = "trade_quantity", nullable = false,
            columnDefinition = "거래량")
    private Long tradeQuantity;

    @Column(name = "trade_price", nullable = false,
            columnDefinition = "거래 대금")
    private Long tradePrice;

    @Column(name = "market_total_amount",nullable = false,
            columnDefinition = "상장 시가 총액")
    private Long marketTotalAmount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
