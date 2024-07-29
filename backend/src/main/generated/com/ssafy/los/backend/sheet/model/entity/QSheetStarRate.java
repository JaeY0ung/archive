package com.ssafy.los.backend.sheet.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSheetStarRate is a Querydsl query type for SheetStarRate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSheetStarRate extends EntityPathBase<SheetStarRate> {

    private static final long serialVersionUID = 1628236057L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSheetStarRate sheetStarRate = new QSheetStarRate("sheetStarRate");

    public final com.ssafy.los.backend.common.model.entity.QBaseEntity _super = new com.ssafy.los.backend.common.model.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QSheet sheet;

    public final NumberPath<Integer> starRate = createNumber("starRate", Integer.class);

    public final com.ssafy.los.backend.user.model.entity.QUser user;

    public QSheetStarRate(String variable) {
        this(SheetStarRate.class, forVariable(variable), INITS);
    }

    public QSheetStarRate(Path<? extends SheetStarRate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSheetStarRate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSheetStarRate(PathMetadata metadata, PathInits inits) {
        this(SheetStarRate.class, metadata, inits);
    }

    public QSheetStarRate(Class<? extends SheetStarRate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.sheet = inits.isInitialized("sheet") ? new QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.user = inits.isInitialized("user") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("user")) : null;
    }

}

