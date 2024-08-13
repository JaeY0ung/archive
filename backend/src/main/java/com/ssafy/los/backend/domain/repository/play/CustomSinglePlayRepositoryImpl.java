package com.ssafy.los.backend.domain.repository.play;


import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.los.backend.domain.entity.QSheet;
import com.ssafy.los.backend.domain.entity.QSinglePlayResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomSinglePlayRepositoryImpl implements CustomSinglePlayRepository {

    private final QSinglePlayResult spr = QSinglePlayResult.singlePlayResult;
    private final QSheet s = QSheet.sheet;
    private final JPAQueryFactory queryFactory;

    @Override
    public Double calculateAvgOfPassedSinglePlayResultByUserId(Long userId) {
        return queryFactory
                .select(s.level.avg())
                .from(spr)
                .join(spr.sheet, s)
                .where(spr.user.id.eq(userId)
                        .and(spr.score.eq(
                                JPAExpressions
                                        .select(spr.score.max())
                                        .from(spr)
                                        .where(
                                                spr.user.id.eq(userId)
                                                        .and(spr.sheet.id.eq(s.id))
                                        )))
                        .and(spr.score.goe(80))
                )
                .fetchOne();
    }
}
