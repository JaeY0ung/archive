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
import com.ssafy.los.backend.domain.entity.*;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForAdminDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CustomSheetRepositoryImpl implements CustomSheetRepository {

    private final JPAQueryFactory queryFactory;

    QSheet s = QSheet.sheet;
    QLikeSheet ls = QLikeSheet.likeSheet;
    QSinglePlayResult spr = QSinglePlayResult.singlePlayResult;
    QSheetStarRate ssr = QSheetStarRate.sheetStarRate;

    @Override
    public List<SheetDetailDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter) {
        List<SheetDetailForUserDto> sheetDetailForUserDtoList = createSelectFromQuery()
                .where(s.deletedAt.isNull(),
                        s.createdAt.isNotNull(),
                        containKeyword(sheetSearchFilter.getKeyword()),
                        inLevels(sheetSearchFilter.getLevels()),
                        inGenre(sheetSearchFilter.getGenres()),
                        inPrice(sheetSearchFilter.getPrices()),
                        isStatusNotRejected()
                )
                .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                .fetch();
        return new ArrayList<>(sheetDetailForUserDtoList);
    }

    @Override
    public List<SheetDetailDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter,
                                                   User loginUser) {
        if (loginUser.getRole().equals("ROLE_ADMIN")) {
            List<SheetDetailForAdminDto> sheetDetailForAdminDtoList = createSelectFromQueryForAdmin()
                    .where(s.deletedAt.isNull(),
                            s.createdAt.isNotNull(),
                            containKeyword(sheetSearchFilter.getKeyword()),
                            inLevels(sheetSearchFilter.getLevels()),
                            inGenre(sheetSearchFilter.getGenres()),
                            inPrice(sheetSearchFilter.getPrices())
                    )
                    .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                    .fetch();
            return new ArrayList<>(sheetDetailForAdminDtoList);
        } else {
            List<SheetDetailForUserDto> sheetDetailForUserDtoList = createSelectFromJoinQuery(
                    sheetSearchFilter.getSuccessStatuses(), loginUser)
                    .where(s.deletedAt.isNull(),
                            s.createdAt.isNotNull(),
                            containKeyword(sheetSearchFilter.getKeyword()),
                            inLevels(sheetSearchFilter.getLevels()),
                            inGenre(sheetSearchFilter.getGenres()),
                            inPrice(sheetSearchFilter.getPrices()),
                            isStatusNotRejected()
                    )
                    .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                    .fetch();
            return new ArrayList<>(sheetDetailForUserDtoList);
        }
    }

    @Override
    public List<SheetDetailDto> findSheetsByStatusForAdmin(Integer status) {
        List<SheetDetailForAdminDto> sheetDetailForAdminDtoList = createSelectFromQueryForAdmin()
                .where(s.deletedAt.isNull(),
                        s.createdAt.isNotNull(),
                        s.status.eq(status)
                )
                .orderBy(createOrderSpecifier(Sort.LATEST))
                .fetch();
        return new ArrayList<>(sheetDetailForAdminDtoList);
    }


    @Override
    public SheetDetailDto findSheetDetailViewDtoById(Long sheetId) {
        return createSelectFromQuery()
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    @Override
    public SheetDetailDto findSheetDetailViewDtoById(Long sheetId, User loginUser) {

        return createSelectFromQuery(loginUser)
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    @Transactional
    @Override
    public long updateViewCount(Long sheetId) {
        return queryFactory.update(s)
                .set(s.viewCount, s.viewCount.add(1))
                .where(s.id.eq(sheetId))
                .execute();
    }

    @Override
    public List<SheetDetailForUserDto> searchByFileName(List<String> fileNames) {
        return createSelectFromQuery()
                .where(s.uuid.in(fileNames))
                .fetch();
    }

    private JPAQuery<SheetDetailForUserDto> createSelectFromQuery(User loginUser) {
        return queryFactory.select(Projections.constructor(SheetDetailForUserDto.class,
                s,
                JPAExpressions.select(ls.count())
                        .from(ls)
                        .where(ls.sheet.eq(s)),
                createLikeStatusExpression(loginUser)
        )).from(s);
    }

    private JPAQuery<SheetDetailForUserDto> createSelectFromQuery() {
        return queryFactory.select(Projections.constructor(SheetDetailForUserDto.class,
                s,
                JPAExpressions.select(ls.count())
                        .from(ls)
                        .where(ls.sheet.eq(s))
        )).from(s);
    }

    private JPAQuery<SheetDetailForAdminDto> createSelectFromQueryForAdmin() {
        return queryFactory.select(Projections.constructor(SheetDetailForAdminDto.class,
                s,
                JPAExpressions.select(ls.count())
                        .from(ls)
                        .where(ls.sheet.eq(s))
        )).from(s);
    }

    private JPAQuery<SheetDetailForUserDto> createSelectFromJoinQuery(
            HashSet<SuccessStatus> successStatuses,
            User loginUser) {

        QSinglePlayResult subSpr = new QSinglePlayResult("subSpr");

        JPAQuery<Float> subQuery = queryFactory
                .select(subSpr.score.max())
                .from(subSpr)
                .where(subSpr.sheet.eq(s).and(subSpr.user.eq(loginUser)));

        if (successStatuses == null || successStatuses.isEmpty()) {
            return createSelectFromQuery(loginUser);
        } else if (successStatuses.size() == 2) {
            return createSelectFromQuery(loginUser)
                    .rightJoin(spr)
                    .on(spr.user.eq(loginUser).and(spr.score.eq(subQuery)));
        }
        for (SuccessStatus successStatus : successStatuses) {
            if (successStatus == SuccessStatus.SUCCESS) {
                return createSelectFromQuery(loginUser)
                        .rightJoin(spr)
                        .on(s.id.eq(spr.sheet.id)
                                .and(spr.user.eq(loginUser))
                                .and(spr.score.eq(subQuery))
                                .and(spr.score.goe(80))
                        );
            } else if (successStatus == SuccessStatus.FAIL) {
                return createSelectFromQuery(loginUser)
                        .rightJoin(spr)
                        .on(s.id.eq(spr.sheet.id)
                                .and(spr.user.eq(loginUser))
                                .and(spr.score.eq(subQuery))
                                .and(spr.score.lt(80))
                        );
            } else {
                throw new IllegalArgumentException("잘못된");
            }
        }
        return createSelectFromQuery(loginUser)
                .rightJoin(spr)
                .on(s.id.eq(spr.sheet.id)
                        .and(spr.user.eq(loginUser))
                        .and(spr.score.goe(80))
                );
    }

    private BooleanExpression createLikeStatusExpression(User loginUser) {
        if (loginUser == null) {
            return Expressions.FALSE;
        }
        return JPAExpressions.selectOne()
                .from(ls)
                .where(ls.sheet.eq(s).and(ls.user.eq(loginUser))).exists();
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
            return Expressions.FALSE;
        }
        return s.level.in(level);
    }

    private BooleanExpression inGenre(Integer[] genre) {
        if (genre == null || genre.length == 0) {
            return Expressions.FALSE;
        }
        return s.song.genre.id.in(genre);
    }

    private BooleanExpression isStatusNotRejected() {
        return s.status.ne(2);
    }

    private BooleanExpression inPrice(Integer[] prices) {
        if (prices == null || prices.length == 0) return Expressions.FALSE;
        if (prices.length == 2) {
            return null;
        }

//        BooleanExpression priceExpression = s.price.eq(0).or(s.price.eq((Integer) null));
        BooleanExpression priceExpression = s.price.eq(0).or(s.price.isNull());

        if (prices[0].equals(1)) {  // 유료
            priceExpression = s.price.gt(0);
        }
        return priceExpression;
    }

    private OrderSpecifier<?> createOrderSpecifier(Sort sort) {
        if (sort == null) {
            return new OrderSpecifier<>(Order.ASC,
                    Expressions.numberTemplate(Double.class, "function('RAND')"));
        }
        return switch (sort) {
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
