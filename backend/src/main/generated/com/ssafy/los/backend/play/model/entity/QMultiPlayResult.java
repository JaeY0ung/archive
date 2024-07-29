package com.ssafy.los.backend.play.model.entity;

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

    private static final long serialVersionUID = 1408246655L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMultiPlayResult multiPlayResult = new QMultiPlayResult("multiPlayResult");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.ssafy.los.backend.user.model.entity.QUser loser;

    public final NumberPath<Float> loserScore = createNumber("loserScore", Float.class);

    public final DateTimePath<java.sql.Timestamp> modifiedAt = createDateTime("modifiedAt", java.sql.Timestamp.class);

    public final com.ssafy.los.backend.sheet.model.entity.QSheet sheet;

    public final StringPath status = createString("status");

    public final com.ssafy.los.backend.user.model.entity.QUser winner;

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
        this.loser = inits.isInitialized("loser") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("loser")) : null;
        this.sheet = inits.isInitialized("sheet") ? new com.ssafy.los.backend.sheet.model.entity.QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.winner = inits.isInitialized("winner") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("winner")) : null;
    }

}

