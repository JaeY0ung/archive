package com.ssafy.los.backend.like.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikeSheet is a Querydsl query type for LikeSheet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikeSheet extends EntityPathBase<LikeSheet> {

    private static final long serialVersionUID = -1472104198L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikeSheet likeSheet = new QLikeSheet("likeSheet");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.ssafy.los.backend.sheet.model.entity.QSheet sheet;

    public final com.ssafy.los.backend.user.model.entity.QUser user;

    public QLikeSheet(String variable) {
        this(LikeSheet.class, forVariable(variable), INITS);
    }

    public QLikeSheet(Path<? extends LikeSheet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikeSheet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikeSheet(PathMetadata metadata, PathInits inits) {
        this(LikeSheet.class, metadata, inits);
    }

    public QLikeSheet(Class<? extends LikeSheet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sheet = inits.isInitialized("sheet") ? new com.ssafy.los.backend.sheet.model.entity.QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.user = inits.isInitialized("user") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("user")) : null;
    }

}

