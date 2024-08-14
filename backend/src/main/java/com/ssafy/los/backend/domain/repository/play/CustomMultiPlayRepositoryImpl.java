package com.ssafy.los.backend.domain.repository.play;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.los.backend.domain.entity.QMultiPlayResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMultiPlayRepositoryImpl implements CustomMultiPlayRepository {

    private final QMultiPlayResult mpr = QMultiPlayResult.multiPlayResult;
    private final JPAQueryFactory queryFactory;

    @Override
    public Long calculateCountOfWinMultiPlayResultByUserId(Long userId) {
        return queryFactory
                .select(mpr.count())
                .from(mpr)
                .where(mpr.winner.id.eq(userId))
                .fetchOne();
    }

    @Override
    public Long calculateCountOfLostMultiPlayResultByUserId(Long userId) {
        return queryFactory
                .select(mpr.count())
                .from(mpr)
                .where(mpr.loser.id.eq(userId))
                .fetchOne();
    }
}
