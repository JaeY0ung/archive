package com.ssafy.los.backend.domain.repository.song;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.los.backend.domain.entity.QSong;
import com.ssafy.los.backend.dto.song.response.SongDto;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomSongRepositoryImpl implements CustomSongRepository {

    private final JPAQueryFactory queryFactory;
    private final QSong s = QSong.song;


    @Override
    public List<SongDto> searchSongsByFilter(String keyword) {
        return queryFactory
                .select(Projections.constructor(SongDto.class,
                        s.id,
                        s.title,
                        s.composer,
                        s.imgName,
                        s.genre))
                .from(s)
                .where(containsKeyword(keyword))
                .fetch();
    }

    @Override
    public SongDto searchSongDtoById(Long songId) {
        return queryFactory
                .select(Projections.constructor(SongDto.class,
                        s.id,
                        s.title,
                        s.composer,
                        s.imgName,
                        s.genre))
                .from(s)
                .where(s.id.eq(songId))
                .fetchOne();
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return s.title.contains(keyword);
    }
}
