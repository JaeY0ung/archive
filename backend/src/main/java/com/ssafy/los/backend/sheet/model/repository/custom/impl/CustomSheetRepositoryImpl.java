package com.ssafy.los.backend.sheet.model.repository.custom.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.los.backend.like.model.entity.QLikeSheet;
import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import com.ssafy.los.backend.sheet.model.entity.QSheet;
import com.ssafy.los.backend.sheet.model.repository.custom.CustomSheetRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomSheetRepositoryImpl implements CustomSheetRepository {

    private final JPAQueryFactory queryFactory;

    QSheet s = QSheet.sheet;
    QLikeSheet ls = QLikeSheet.likeSheet;

    public List<SheetResponseDto> findSheets(String keyword, String sort, Long userId) {
        return queryFactory.select(Projections.constructor(SheetResponseDto.class,
                        s,
                        JPAExpressions.select(ls.count())
                                .from(ls)
                                .where(ls.sheet.eq(s)),
                        createLikeStatusExpression(userId)
                ))
                .from(s)
                .where(s.deletedAt.isNull(), s.createdAt.isNotNull(), containKeyword(keyword)) // 현재 존재하는
                .orderBy(createOrderSpecifier(sort)) // 날짜.
                .fetch();
    }

    @Override
    public List<SheetResponseDto> findSheets(String keyword, String sort) {
        return queryFactory.select(Projections.constructor(SheetResponseDto.class,
                        s,
                        JPAExpressions.select(ls.count())
                                .from(ls)
                                .where(ls.sheet.eq(s))
                ))
                .from(s)
                .where(s.deletedAt.isNull(), s.createdAt.isNotNull(), containKeyword(keyword)) // 현재 존재하는
                .orderBy(createOrderSpecifier(sort)) // 날짜.
                .fetch();
    }

    @Override
    public SheetResponseDto findSheetById(Long sheetId, Long userId) {
        return queryFactory.select(Projections.constructor(SheetResponseDto.class,
                        s,
                        JPAExpressions.select(ls.count())
                                .from(ls)
                                .where(ls.sheet.eq(s)),
                        createLikeStatusExpression(userId)
                ))
                .from(s)
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    @Override
    public SheetResponseDto findSheetById(Long sheetId) {
        return queryFactory.select(Projections.constructor(SheetResponseDto.class,
                        s,
                        JPAExpressions.select(ls.count())
                                .from(ls)
                                .where(ls.sheet.eq(s))
                ))
                .from(s)
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    private BooleanExpression createLikeStatusExpression(Long userId) {
        if (userId == null) {
            return Expressions.TRUE;
        }
        return JPAExpressions.selectOne()
                .from(ls)
                .where(ls.sheet.eq(s).and(ls.user.id.eq(userId))).exists();
    }

    private BooleanExpression containKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Expressions.TRUE;
        }
        return s.title.eq(keyword);
    }

    private OrderSpecifier createOrderSpecifier(String sort) {
        if (sort == null || sort.isEmpty()) {
            return null;
        }
        return switch (sort) {
            // TODO : LikeSheet과 join해서 가져오는것을 엔티티에서 OneToMany로 설정하는게 맞는지?...
            case "popular" -> new OrderSpecifier<>(Order.DESC,
                    JPAExpressions.select(ls.count())
                            .from(ls)
                            .where(ls.sheet.eq(s))
            );
            case "oldest" -> new OrderSpecifier<>(Order.ASC, s.createdAt);
            case "cheapest" -> new OrderSpecifier<>(Order.ASC, s.price);
            case "viewHighest" -> new OrderSpecifier<>(Order.ASC, s.viewCount);
            case "latest" -> new OrderSpecifier<>(Order.DESC, s.createdAt);
            default -> new OrderSpecifier<>(Order.DESC, s.createdAt);
        };
    }
}
