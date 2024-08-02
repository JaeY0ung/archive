package com.ssafy.los.backend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSinglePlayResult is a Querydsl query type for SinglePlayResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSinglePlayResult extends EntityPathBase<SinglePlayResult> {

    private static final long serialVersionUID = 1631379929L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSinglePlayResult singlePlayResult = new QSinglePlayResult("singlePlayResult");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> playTime = createNumber("playTime", Long.class);

    public final NumberPath<Float> score = createNumber("score", Float.class);

    public final QSheet sheet;

    public final QUser user;

    public QSinglePlayResult(String variable) {
        this(SinglePlayResult.class, forVariable(variable), INITS);
    }

    public QSinglePlayResult(Path<? extends SinglePlayResult> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSinglePlayResult(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSinglePlayResult(PathMetadata metadata, PathInits inits) {
        this(SinglePlayResult.class, metadata, inits);
    }

    public QSinglePlayResult(Class<? extends SinglePlayResult> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sheet = inits.isInitialized("sheet") ? new QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

