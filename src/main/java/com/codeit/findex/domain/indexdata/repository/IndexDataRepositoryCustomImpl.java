package com.codeit.findex.domain.indexdata.repository;

import com.codeit.findex.domain.indexdata.dto.IndexDataSearchRequest;
import com.codeit.findex.domain.indexdata.entity.IndexData;
import com.codeit.findex.domain.indexdata.entity.QIndexData;
import com.codeit.findex.global.util.UuidResolver;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class IndexDataRepositoryCustomImpl implements IndexDataRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final UuidResolver uuidResolver;

    private static final QIndexData indexData = QIndexData.indexData;

    @Override
    public Slice<IndexData> findListByFilterAndCursor(IndexDataSearchRequest searchRequest) {

        int pageSize = Optional.ofNullable(searchRequest.size())
                .filter(s -> s > 0)
                .orElse(10);

        String sortField = searchRequest.sortField() == null
                ? "baseDate"
                : searchRequest.sortField();

        boolean isAsc = "asc".equalsIgnoreCase(searchRequest.sortDirection());

        // size+1을 조회하여 다음 페이지가 존재하는지 확인
        Order order = isAsc ? Order.ASC : Order.DESC;
        PathBuilder<IndexData> entityPath = new PathBuilder<>(IndexData.class, "indexData");

        List<IndexData> content = queryFactory
                .selectFrom(indexData)
                .where(
                        eqIndexInfoId(searchRequest.indexInfoId()),
                        betweenBaseDate(searchRequest.startDate(), searchRequest.endDate()),
                        dynamicCursorCondition(sortField, searchRequest.cursor(), searchRequest.idAfter(), isAsc)
                )
                .orderBy(
                        new OrderSpecifier<>(order, entityPath.getComparable(sortField, Comparable.class)),
                        new OrderSpecifier<>(order, indexData.id)
                )
                .limit(pageSize + 1)
                .fetch();

        content = new ArrayList<>(content);

        boolean hasNext = false;
        if (content.size() > pageSize) {
            hasNext = true;
            content.remove(pageSize);
        }

        return new SliceImpl<>(content, PageRequest.of(0, pageSize), hasNext);
    }

    @Override
    public Integer countByFilter(IndexDataSearchRequest searchRequest) {
        Long totalCount = queryFactory
                .select(indexData.count())
                .from(indexData)
                .where(
                        eqIndexInfoId(searchRequest.indexInfoId()),
                        betweenBaseDate(searchRequest.startDate(), searchRequest.endDate())
                )
                .fetchOne();

        return totalCount != null ? totalCount.intValue() : 0;
    }

    private BooleanExpression eqIndexInfoId(String indexInfoId) {
        if (indexInfoId == null || indexInfoId.isBlank()) return null;
        UUID uuid = uuidResolver.resolve(indexInfoId);
        return uuid != null ? indexData.indexInfo.id.eq(uuid) : null;
    }

    private BooleanExpression betweenBaseDate(LocalDate startDate, LocalDate endDate) {
        return (startDate != null && endDate != null) ? indexData.baseDate.between(startDate, endDate) : null;
    }

    private BooleanExpression dynamicCursorCondition(String sortField, String cursorValue, UUID idAfter, boolean isAsc) {
        if (cursorValue == null || idAfter == null) {
            return null; // 첫 페이지 요청 시 커서 조건 제외
        }

        PathBuilder<IndexData> entityPath = new PathBuilder<>(IndexData.class, "indexData");
        var targetField = entityPath.getComparable(sortField, Comparable.class);

        Comparable<?> cursor;
        if("baseDate".equals(sortField)) {
            cursor = LocalDate.parse(cursorValue);
        }else{
            cursor = cursorValue;
        }

        if (isAsc) {
            return targetField.gt(cursor)
                    .or(targetField.eq(cursor).and(indexData.id.gt(idAfter)));
        } else {
            return targetField.lt(cursor)
                    .or(targetField.eq(cursor).and(indexData.id.lt(idAfter)));
        }
    }

}
