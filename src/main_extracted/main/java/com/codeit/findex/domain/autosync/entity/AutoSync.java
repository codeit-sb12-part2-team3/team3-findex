package com.codeit.findex.domain.autosync.entity;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "auto_sync")
public class AutoSync {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    // 새로운 엔티티 생성을 위한 생성자
    public AutoSync(IndexInfo indexInfo, Boolean enabled) {
        this.indexInfo = indexInfo;
        this.enabled = enabled;
    }
}