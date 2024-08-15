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
import com.ssafy.los.backend.domain.entity.QOrder;
import com.ssafy.los.backend.domain.entity.QOrderSheet;
import com.ssafy.los.backend.domain.entity.QSheet;
import com.ssafy.los.backend.domain.entity.QSheetStarRate;
import com.ssafy.los.backend.domain.entity.QSinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForAdminDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import java.util.ArrayList;
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
    QOrder o = QOrder.order;
    QOrderSheet os = QOrderSheet.orderSheet;

    @Override
    public List<SheetDetailDto> findSheetsByFilterAndLoginUser(SheetSearchFilter sheetSearchFilter,
            User loginUser) {
        if (sheetSearchFilter.getPage() == null) {
            sheetSearchFilter.setPage(0);
        }

        if (loginUser == null) { // 비 로그인 유저
            List<SheetDetailForUserDto> sheetDetailForUserDtoList = createSelectFromQuery(null)
                    .where(createWhereClause(sheetSearchFilter), inStatuses(new Integer[]{0, 1}))
                    .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                    .limit(sheetSearchFilter.getSize()) // limit 추가
                    .offset((long) sheetSearchFilter.getPage()
                            * sheetSearchFilter.getSize()) // offset 추가
                    .fetch();
            return new ArrayList<>(sheetDetailForUserDtoList);
        } else if (loginUser.getRole().equals("ROLE_ADMIN")) { // 관리자 계정
            List<SheetDetailForAdminDto> sheetDetailForAdminDtoList = createSelectFromQueryForAdmin()
                    .where(createWhereClause(sheetSearchFilter), inStatuses(new Integer[]{0, 1, 2}))
                    .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                    .limit(sheetSearchFilter.getSize()) // limit 추가
                    .offset((long) sheetSearchFilter.getPage()
                            * sheetSearchFilter.getSize()) // offset 추가
                    .fetch();
            return new ArrayList<>(sheetDetailForAdminDtoList);
        } else { // 로그인 유저
            List<SheetDetailForUserDto> sheetDetailForUserDtoList = createSelectFromJoinQuery(
                    sheetSearchFilter.getSuccessStatuses(), loginUser)
                    .where(createWhereClause(sheetSearchFilter), inStatuses(new Integer[]{0, 1}))
                    .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                    .limit(sheetSearchFilter.getSize())
                    .offset((long) sheetSearchFilter.getPage()
                            * sheetSearchFilter.getSize())
                    .fetch();
            return new ArrayList<>(sheetDetailForUserDtoList);
        }
    }

    // Admin 악보 관리 페이지 에서 사용
    @Override
    public List<SheetDetailDto> findSheetsByStatusForAdmin(SheetSearchFilter sheetSearchFilter) {
        List<SheetDetailForAdminDto> sheetDetailForAdminDtoList = createSelectFromQueryForAdmin()
                .where(createWhereClause(sheetSearchFilter),
                        inStatuses(sheetSearchFilter.getStatuses()))
                .orderBy(createOrderSpecifier(sheetSearchFilter.getSort()))
                .fetch();
        return new ArrayList<>(sheetDetailForAdminDtoList);
    }

    @Override
    public SheetDetailDto findSheetDetailViewDtoById(Long sheetId, User loginUser) {
        return createSelectFromQuery(loginUser, sheetId)
                .where(s.id.eq(sheetId), s.deletedAt.isNull(), s.createdAt.isNotNull())
                .fetchOne();
    }

    @Override
    public SheetDetailDto searchOneRecentSinglePlayedSheet(User loginUser) {
        return createSelectFromQuery(loginUser)
                .where(
                        s.deletedAt.isNull(),
                        s.createdAt.isNotNull()
                )
                .rightJoin(spr)
                .on(spr.user.eq(loginUser).and(spr.sheet.eq(s)))
                .orderBy(spr.createdAt.desc())
                .fetchFirst();
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
    public List<SheetDetailForUserDto> searchByFileNames(List<String> fileNames) {
        return createSelectFromQuery(null)
                .where(s.uuid.in(fileNames))
                .fetch();
    }

    @Override
    public List<SheetDetailForUserDto> searchByUserLike(Long userId) {
        return queryFactory
                .select(Projections.constructor(SheetDetailForUserDto.class,
                        s,
                        JPAExpressions.select(ls.count())
                                .from(ls)
                                .where(ls.sheet.eq(s)),
                        Expressions.constant(true)))
                .from(ls)
                .join(ls.sheet, s)
                .where(ls.user.id.eq(userId)
                        .and(s.deletedAt.isNull())
                        .and(s.createdAt.isNotNull())
                        .and(inStatuses(new Integer[]{0, 1})))
                .orderBy(s.createdAt.desc())
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


    private JPAQuery<SheetDetailForUserDto> createSelectFromQuery(User loginUser, Long sheetId) {
        return queryFactory.select(Projections.constructor(SheetDetailForUserDto.class,
                s,
                JPAExpressions.select(ls.count())
                        .from(ls)
                        .where(ls.sheet.eq(s)),
                createLikeStatusExpression(loginUser),
                JPAExpressions.selectOne()
                        .from(o)
                        .where(o.user.eq(loginUser),
                                o.orderSheetList.contains(
                                        JPAExpressions
                                                .selectFrom(os)
                                                .where(os.sheet.id.eq(sheetId))))
                        .exists()
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

    private BooleanExpression createWhereClause(SheetSearchFilter sheetSearchFilter) {
        return s.deletedAt.isNull()
                .and(s.createdAt.isNotNull())
                .and(containKeyword(sheetSearchFilter.getKeyword()))
                .and(inLevels(sheetSearchFilter.getLevels()))
                .and(inGenre(sheetSearchFilter.getGenres()))
                .and(inPrice(sheetSearchFilter.getPrices()));
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
        if (level == null) {
            return null;
        } else if (level.length == 0) {
            return Expressions.FALSE;
        }
        return s.level.in(level);
    }

    private BooleanExpression inGenre(Integer[] genre) {
        if (genre == null) {
            return null;
        } else if (genre.length == 0) {
            return Expressions.FALSE;
        }
        return s.song.genre.id.in(genre);
    }

    private BooleanExpression inStatuses(Integer[] statuses) {
        return s.status.in(statuses);
    }

    private BooleanExpression inPrice(Integer[] prices) {
        if (prices == null || prices.length == 2) {
            return null;
        } else if (prices.length == 0) {
            return Expressions.FALSE;
        }

        if (prices[0].equals(1)) {  // 유료
            return s.price.gt(0);
        } else {
            return s.price.isNull().or(s.price.eq(0));
        }
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
