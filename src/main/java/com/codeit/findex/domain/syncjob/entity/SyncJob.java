package com.codeit.findex.domain.syncjob.entity;

import com.codeit.findex.domain.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "sync_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SyncJob {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "job_type", nullable = false, length = 20)
    private String jobType;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "worker", nullable = false, length = 45)
    private String worker;

    @Column(name = "job_time", nullable = false)
    private LocalDateTime jobTime;

    @Column(name = "result", nullable = false, length = 20)
    private String result;


}
