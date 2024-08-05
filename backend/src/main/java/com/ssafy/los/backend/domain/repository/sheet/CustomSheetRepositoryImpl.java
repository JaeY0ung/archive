package com.ssafy.los.backend.domain.repository.sheet;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.los.backend.constant.Sort;
import com.ssafy.los.backend.constant.SuccessStatus;
import com.ssafy.los.backend.domain.entity.QLikeSheet;
import com.ssafy.los.backend.domain.entity.QSheet;
import com.ssafy.los.backend.domain.entity.QSheetStarRate;
import com.ssafy.los.backend.domain.entity.QSinglePlayResult;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailViewDto;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CustomSheetRepositoryImpl implements CustomSheetRepository {

    private final JPAQueryFactory queryFactory;

    QSheet s = QSheet.sheet;
    QLikeSheet ls = QLikeSheet.likeSheet;
    QSinglePlayResult spr = QSinglePlayResult.singlePlayResult;
    QSheetStarRate ssr = QSheetStarRate.sheetStarRate;

    @Override
    public List<SheetDetailViewDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter) {
        return createSelectFromQuery()
                .where(s.deletedAt.isNull(),
                        s.createdAt.isNotNull(),
                        containKeyword(sheetSearchFilter.getKeyword()),
                        inLevels(sheetSearchFilter.getLevels()),
                        inGenre(sheetSearchFilter.getGenres()),
                        inPrice(sheetSearchFilter.getPrices())
                )
                .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                .fetch();
    }

    @Override
    public List<SheetDetailViewDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter, Long userId) {
        return createSelectFromJoinQuery(sheetSearchFilter.getSuccessStatuses(), userId)
                .where(s.deletedAt.isNull(),
                        s.createdAt.isNotNull(),
                        containKeyword(sheetSearchFilter.getKeyword()),
                        inLevels(sheetSearchFilter.getLevels()),
                        inGenre(sheetSearchFilter.getGenres()),
                        inPrice(sheetSearchFilter.getPrices())
                )
                .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                .fetch();
    }

    @Override
    public SheetDetailViewDto findSheetDetailViewDtoById(Long sheetId) {
        return createSelectFromQuery()
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    @Override
    public SheetDetailViewDto findSheetDetailViewDtoById(Long sheetId, Long userId) {

        return createSelectFromQuery(userId)
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    @Transactional
    @Override
    public long updateViewCount(Long sheetId) {
        return queryFactory.update(s)
                .set(s.viewCount, s.viewCount.add(1))
                .execute();
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

    private JPAQuery<SheetDetailViewDto> createSelectFromJoinQuery(HashSet<SuccessStatus> successStatuses,
            long userId) {

        QSinglePlayResult subSpr = new QSinglePlayResult("subSpr");

        JPAQuery<Float> subQuery = queryFactory
                .select(subSpr.score.max())
                .from(subSpr)
                .where(subSpr.sheet.eq(s).and(subSpr.user.id.eq(userId)));

        if (successStatuses == null || successStatuses.isEmpty()) {
            return createSelectFromQuery();
        } else if (successStatuses.size() == 2) {
            return createSelectFromQuery()
                    .rightJoin(spr)
                    .on(spr.user.id.eq(userId).and(spr.score.eq(subQuery)));
        }
        for (SuccessStatus successStatus : successStatuses) {
            if (successStatus == SuccessStatus.SUCCESS) {
                return createSelectFromQuery()
                        .rightJoin(spr)
                        .on(s.id.eq(spr.sheet.id)
                                .and(spr.user.id.eq(userId))
                                .and(spr.score.eq(subQuery))
                                .and(spr.score.goe(80))
                        );
            } else if (successStatus == SuccessStatus.FAIL) {
                return createSelectFromQuery()
                        .rightJoin(spr)
                        .on(s.id.eq(spr.sheet.id)
                                .and(spr.user.id.eq(userId))
                                .and(spr.score.eq(subQuery))
                                .and(spr.score.lt(80))
                        );
            } else {
                throw new IllegalArgumentException("잘못된");
            }
        }
        return createSelectFromQuery()
                .rightJoin(spr)
                .on(s.id.eq(spr.sheet.id)
                        .and(spr.user.id.eq(userId))
                        .and(spr.score.goe(80))
                );
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

    private BooleanExpression inLevels(Integer[] level) {
        if (level == null || level.length == 0) {
            return null;
        }
        return s.level.in(level);
    }

    private BooleanExpression inGenre(Integer[] genre) {
        if (genre == null || genre.length == 0) {
            return null;
        }
        return s.song.genre.id.in(genre);
    }

    private BooleanExpression inPrice(Integer[] price) {
        if (price == null || price.length == 0) {
            return null;
        }

        BooleanExpression priceExpression = null;
        for (Integer p : price) {
            if (p == 0) {  // 무료
                if (priceExpression == null) {
                    priceExpression = s.price.eq(0);
                } else {
                    priceExpression = priceExpression.or(s.price.eq(0));
                }
            } else if (p == 1) {  // 유료
                if (priceExpression == null) {
                    priceExpression = s.price.gt(0);
                } else {
                    priceExpression = priceExpression.or(s.price.gt(0));
                }
            }
        }
        return priceExpression;
    }

    private OrderSpecifier<?> createOrderSpecifier(Sort sort) {
        if (sort == null) {
            return new OrderSpecifier<>(Order.ASC,
                    Expressions.numberTemplate(Double.class, "function('RAND')"));
        }
        log.info(sort.toString());
        return switch (sort) {
            // TODO : LikeSheet과 join해서 가져오는것을 엔티티에서 OneToMany로 설정하는게 맞는지?...
            case POPULAR -> new OrderSpecifier<>(Order.DESC,
                    JPAExpressions.select(ls.count())
                            .from(ls)
                            .where(ls.sheet.eq(s))
            );
//            case OLDEST -> new OrderSpecifier<>(Order.ASC, s.createdAt);
//            case CHEAPEST -> new OrderSpecifier<>(Order.ASC, s.price);
            case HIGHEST_VIEW -> new OrderSpecifier<>(Order.DESC, s.viewCount);
            case LATEST -> new OrderSpecifier<>(Order.DESC, s.createdAt);
            case RANDOM -> new OrderSpecifier<>(Order.ASC,
                    Expressions.numberTemplate(Double.class, "function('RAND')"));
            case HIGHEST_STAR_RATE -> new OrderSpecifier<>(Order.DESC,
                    JPAExpressions.select(ssr.starRate.avg())
                            .from(ssr)
                            .where(ssr.sheet.eq(s))
            );
            default -> new OrderSpecifier<>(Order.DESC, s.createdAt);
        };
    }
}
