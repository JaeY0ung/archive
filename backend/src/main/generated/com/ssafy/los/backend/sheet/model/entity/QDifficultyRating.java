package com.ssafy.los.backend.sheet.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDifficultyRating is a Querydsl query type for DifficultyRating
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDifficultyRating extends EntityPathBase<DifficultyRating> {

    private static final long serialVersionUID = -516509712L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDifficultyRating difficultyRating = new QDifficultyRating("difficultyRating");

    public final com.ssafy.los.backend.common.model.entity.QBaseEntity _super = new com.ssafy.los.backend.common.model.entity.QBaseEntity(this);

    public final StringPath contents = createString("contents");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created_at = _super.created_at;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modified_at = _super.modified_at;

    public final QSheet sheet;

    public final com.ssafy.los.backend.user.model.entity.QUser user;

    public QDifficultyRating(String variable) {
        this(DifficultyRating.class, forVariable(variable), INITS);
    }

    public QDifficultyRating(Path<? extends DifficultyRating> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDifficultyRating(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDifficultyRating(PathMetadata metadata, PathInits inits) {
        this(DifficultyRating.class, metadata, inits);
    }

    public QDifficultyRating(Class<? extends DifficultyRating> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sheet = inits.isInitialized("sheet") ? new QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.user = inits.isInitialized("user") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("user")) : null;
    }

}

