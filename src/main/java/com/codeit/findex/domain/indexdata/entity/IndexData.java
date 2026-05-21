package com.codeit.findex.domain.indexdata.entity;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "index_data",
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_indexdata_indexid_basedate",
                columnNames = {"index_id","base_date"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @ToString
@Builder
public class IndexData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(
            name = "index_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private UUID indexInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "source_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(name = "market_price", nullable = false)
    private BigDecimal marketPrice;

    @Column(name = "closing_price", nullable = false)
    private BigDecimal closingPrice;

    @Column(name = "high_price", nullable = false)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false)
    private BigDecimal lowPrice;

    @Column(name = "versus", nullable = false)
    private BigDecimal versus;

    @Column(name = "fluctuation_rate",nullable = false)
    private BigDecimal fluctuationRate;

    @Column(name = "trade_quantity", nullable = false)
    private Long tradingQuantity;

    @Column(name = "trade_price", nullable = false)
    private Long tradingPrice;

    @Column(name = "market_total_amount",nullable = false)
    private Long marketTotalAmount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateValues(
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount
    ) {
        this.marketPrice = marketPrice;
        this.closingPrice = closingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.versus = versus;
        this.fluctuationRate = fluctuationRate;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.marketTotalAmount = marketTotalAmount;
    }
}
