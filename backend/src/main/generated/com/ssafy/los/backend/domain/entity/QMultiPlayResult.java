package com.ssafy.los.backend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMultiPlayResult is a Querydsl query type for MultiPlayResult
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMultiPlayResult extends EntityPathBase<MultiPlayResult> {

    private static final long serialVersionUID = -1592478838L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMultiPlayResult multiPlayResult = new QMultiPlayResult("multiPlayResult");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDraw = createBoolean("isDraw");

    public final QUser loser;

    public final NumberPath<Float> loserScore = createNumber("loserScore", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> playTime = createNumber("playTime", Long.class);

    public final QSheet sheet;

    public final BooleanPath status = createBoolean("status");

    public final QUser winner;

    public final NumberPath<Float> winnerScore = createNumber("winnerScore", Float.class);

    public QMultiPlayResult(String variable) {
        this(MultiPlayResult.class, forVariable(variable), INITS);
    }

    public QMultiPlayResult(Path<? extends MultiPlayResult> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMultiPlayResult(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMultiPlayResult(PathMetadata metadata, PathInits inits) {
        this(MultiPlayResult.class, metadata, inits);
    }

    public QMultiPlayResult(Class<? extends MultiPlayResult> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.loser = inits.isInitialized("loser") ? new QUser(forProperty("loser")) : null;
        this.sheet = inits.isInitialized("sheet") ? new QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.winner = inits.isInitialized("winner") ? new QUser(forProperty("winner")) : null;
    }

}

