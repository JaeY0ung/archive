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
                                .where(ls.sheet.eq(s))
// 악보 리스트 가져올 때는 유저의 좋아요 정보 가져오지 않는다.
//                        createLikeStatusExpression(userId)
                ))
                .from(s)
                .where(s.deletedAt.isNull(), s.createdAt.isNotNull(),
                        containKeyword(keyword))
                .orderBy(createOrderSpecifier(sort))
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
    public List<SheetResponseDto> findSheetsByLevelRandomly(Integer level, Long userId) {
        return queryFactory.select(Projections.constructor(SheetResponseDto.class,
                        s,
                        JPAExpressions.select(ls.count())
                                .from(ls)
                                .where(ls.sheet.eq(s))
// 악보 리스트 가져올 때는 유저의 좋아요 정보 가져오지 않는다.
//                        createLikeStatusExpression(userId)
                ))
                .from(s)
                .where(s.deletedAt.isNull(), s.createdAt.isNotNull(),
                        equalLevel(level))
                .orderBy(createOrderSpecifier("RANDOM"))
                .fetch();
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

    private OrderSpecifier createOrderSpecifier(String sort) {
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
