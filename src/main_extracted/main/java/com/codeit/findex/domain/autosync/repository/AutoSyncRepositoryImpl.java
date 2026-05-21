package com.codeit.findex.domain.autosync.repository;

import com.codeit.findex.domain.autosync.entity.AutoSync;
import com.codeit.findex.domain.autosync.entity.QAutoSync;
import com.codeit.findex.domain.indexinfo.entity.QIndexInfo;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

public class AutoSyncRepositoryImpl implements AutoSyncRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AutoSyncRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<AutoSync> findListByFilterAndCursor(UUID nextIdAfter, UUID indexId, Boolean enabled, String sort, int size) {
        QAutoSync autoSync = QAutoSync.autoSync;
        QIndexInfo indexInfo = QIndexInfo.indexInfo;

        return queryFactory
                .selectFrom(autoSync)
                .join(autoSync.indexInfo, indexInfo).fetchJoin() // N+1 문제 차단
                .where(
                        eqIndexId(indexId),    // 지수 필터링
                        eqEnabled(enabled),    // 활성화 필터링
                        gtNextIdAfter(nextIdAfter) // 커서 페이징
                )
                .orderBy(getSortOrder(sort))   // 정렬 조건 적용
                .limit(size)                   // 요청한 개수만큼만
                .fetch();
    }

    @Override
    public long countByFilter(UUID indexId, Boolean enabled) {
        QAutoSync autoSync = QAutoSync.autoSync;

        Long count = queryFactory
                .select(autoSync.count())
                .from(autoSync)
                .where(
                        eqIndexId(indexId),
                        eqEnabled(enabled)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    // 동적 쿼리 (null을 반환 -> 알아서 제외됨)

    private BooleanExpression eqIndexId(UUID indexId) {
        return indexId != null ? QAutoSync.autoSync.indexInfo.id.eq(indexId) : null;
    }

    private BooleanExpression eqEnabled(Boolean enabled) {
        return enabled != null ? QAutoSync.autoSync.enabled.eq(enabled) : null;
    }

    private BooleanExpression gtNextIdAfter(UUID nextIdAfter) {
        // 정확한 페이지네이션을 위해 {이전 페이지의 마지막 요소 ID}보다 큰 것부터 조회
        return nextIdAfter != null ? QAutoSync.autoSync.id.gt(nextIdAfter) : null;
    }

    private OrderSpecifier<?> getSortOrder(String sort) {
        QAutoSync autoSync = QAutoSync.autoSync;

        if ("index".equalsIgnoreCase(sort)) {
            return autoSync.indexInfo.indexName.asc(); // 지수명 기준 정렬
        } else if ("enabled".equalsIgnoreCase(sort)) {
            return autoSync.enabled.desc();            // 활성화 여부 기준 정렬
        }

        return autoSync.id.asc(); // 기본 정렬값 (ID 순서)
    }
}