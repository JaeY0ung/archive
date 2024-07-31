package com.ssafy.los.backend.sheet.model.repository.custom.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.los.backend.like.model.entity.QLikeSheet;
import com.ssafy.los.backend.sheet.model.dto.request.SheetSearchFilter;
import com.ssafy.los.backend.sheet.model.dto.response.SheetDetailViewDto;
import com.ssafy.los.backend.sheet.model.entity.QSheet;
import com.ssafy.los.backend.sheet.model.repository.custom.CustomSheetRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomSheetRepositoryImpl implements CustomSheetRepository {

    private final JPAQueryFactory queryFactory;

    QSheet s = QSheet.sheet;
    QLikeSheet ls = QLikeSheet.likeSheet;

    @Override
    public List<SheetDetailViewDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter) {
        return createSelectFromQuery()
                .where(s.deletedAt.isNull(), s.createdAt.isNotNull(),
                        containKeyword(sheetSearchFilter.getKeyword()),
                        equalLevel(sheetSearchFilter.getLevel()))
                .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                .fetch();
    }

    @Override
    public SheetDetailViewDto findSheetDetailViewDtoById(Long sheetId, Long userId) {

        return createSelectFromQuery(userId)
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    private JPAQuery<SheetDetailViewDto> createSelectFromQuery(Long userId) {
        return queryFactory.select(Projections.constructor(SheetDetailViewDto.class,
                s,
                JPAExpressions.select(ls.count())
                        .from(ls)
                        .where(ls.sheet.eq(s)),
                createLikeStatusExpression(userId)

        )).from(s);
    }

    private JPAQuery<SheetDetailViewDto> createSelectFromQuery() {
        return queryFactory.select(Projections.constructor(SheetDetailViewDto.class,
                s,
                JPAExpressions.select(ls.count())
                        .from(ls)
                        .where(ls.sheet.eq(s))
        )).from(s);
    }

    private BooleanExpression createLikeStatusExpression(Long userId) {
        if (userId == null) {
            return Expressions.FALSE;
        }
        return JPAExpressions.selectOne()
                .from(ls)
                .where(ls.sheet.eq(s).and(ls.user.id.eq(userId))).exists();
    }

    private BooleanExpression containKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return s.title.contains(keyword)
                .or(s.uploader.nickname.contains(keyword))
                .or(s.song.title.contains(keyword))
                .or(s.song.composer.contains(keyword));
    }

    private BooleanExpression equalLevel(Integer level) {
        if (level == null || level == 0) {
            return null;
        }
        return s.level.eq(level);
    }

    private OrderSpecifier<?> createOrderSpecifier(String sort) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.ASC,
                    Expressions.numberTemplate(Double.class, "function('RAND')"));
        }
        return switch (sort) {
            // TODO : LikeSheet과 join해서 가져오는것을 엔티티에서 OneToMany로 설정하는게 맞는지?...
            case "POPULAR" -> new OrderSpecifier<>(Order.DESC,
                    JPAExpressions.select(ls.count())
                            .from(ls)
                            .where(ls.sheet.eq(s))
            );
            case "OLDEST" -> new OrderSpecifier<>(Order.ASC, s.createdAt);
            case "CHEAPEST" -> new OrderSpecifier<>(Order.ASC, s.price);
            case "HIGHEST_VIEW" -> new OrderSpecifier<>(Order.DESC, s.viewCount);
            case "LATEST" -> new OrderSpecifier<>(Order.DESC, s.createdAt);
            case "RANDOM" -> new OrderSpecifier<>(Order.ASC,
                    Expressions.numberTemplate(Double.class, "function('RAND')"));
            default -> new OrderSpecifier<>(Order.DESC, s.createdAt);
        };
    }
}
