package com.codeit.findex.domain.syncjob.specification;

import com.codeit.findex.domain.syncjob.dto.SyncJobSearchCondition;
import com.codeit.findex.domain.syncjob.entity.SyncJob;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SyncJobSpecification {

    public static Specification<SyncJob> withCondition(
            SyncJobSearchCondition condition
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (condition.jobType() != null) {
                predicates.add(
                        cb.equal(root.get("jobType"), condition.jobType())
                );
            }

            if (condition.indexId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("indexInfo").get("id"),
                                condition.indexId()
                        )
                );
            }

            if (condition.targetDate() != null) {
                predicates.add(
                        cb.equal(root.get("targetDate"), condition.targetDate())
                );
            }

            if (condition.worker() != null) {
                predicates.add(
                        cb.equal(root.get("worker"), condition.worker())
                );
            }

            if (condition.result() != null) {
                predicates.add(
                        cb.equal(root.get("result"), condition.result())
                );
            }

            if (condition.jobTimeFrom() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("jobTime"),
                                condition.jobTimeFrom()
                        )
                );
            }

            if (condition.jobTimeTo() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("jobTime"),
                                condition.jobTimeTo()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<SyncJob> cursor(LocalDateTime lastJobTime) {

        return (root, query, cb) -> {

            if (lastJobTime == null) {
                return null;
            }

            return cb.lessThan(root.get("jobTime"), lastJobTime);
        };
    }
}