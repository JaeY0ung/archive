package com.ssafy.los.backend.sheet.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSheet is a Querydsl query type for Sheet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSheet extends EntityPathBase<Sheet> {

    private static final long serialVersionUID = 373149095L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSheet sheet = new QSheet("sheet");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> deletedAt = createDateTime("deletedAt", java.sql.Timestamp.class);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final ListPath<com.ssafy.los.backend.like.model.entity.LikeSheet, com.ssafy.los.backend.like.model.entity.QLikeSheet> likeSheets = this.<com.ssafy.los.backend.like.model.entity.LikeSheet, com.ssafy.los.backend.like.model.entity.QLikeSheet>createList("likeSheets", com.ssafy.los.backend.like.model.entity.LikeSheet.class, com.ssafy.los.backend.like.model.entity.QLikeSheet.class, PathInits.DIRECT2);

    public final DateTimePath<java.sql.Timestamp> modifiedAt = createDateTime("modifiedAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final com.ssafy.los.backend.song.model.entity.QSong song;

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final StringPath title = createString("title");

    public final com.ssafy.los.backend.user.model.entity.QUser uploader;

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QSheet(String variable) {
        this(Sheet.class, forVariable(variable), INITS);
    }

    public QSheet(Path<? extends Sheet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSheet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSheet(PathMetadata metadata, PathInits inits) {
        this(Sheet.class, metadata, inits);
    }

    public QSheet(Class<? extends Sheet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.song = inits.isInitialized("song") ? new com.ssafy.los.backend.song.model.entity.QSong(forProperty("song"), inits.get("song")) : null;
        this.uploader = inits.isInitialized("uploader") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("uploader")) : null;
    }

}

