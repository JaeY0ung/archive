package com.ssafy.los.backend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDifficulty is a Querydsl query type for Difficulty
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDifficulty extends EntityPathBase<Difficulty> {

    private static final long serialVersionUID = 1313630907L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDifficulty difficulty = new QDifficulty("difficulty");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QSheet sheet;

    public final QUser user;

    public QDifficulty(String variable) {
        this(Difficulty.class, forVariable(variable), INITS);
    }

    public QDifficulty(Path<? extends Difficulty> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDifficulty(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDifficulty(PathMetadata metadata, PathInits inits) {
        this(Difficulty.class, metadata, inits);
    }

    public QDifficulty(Class<? extends Difficulty> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sheet = inits.isInitialized("sheet") ? new QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

