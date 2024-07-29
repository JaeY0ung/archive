package com.ssafy.los.backend.alert.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlert is a Querydsl query type for Alert
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlert extends EntityPathBase<Alert> {

    private static final long serialVersionUID = 699452039L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlert alert = new QAlert("alert");

    public final QAlertType alertType;

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath readStatus = createBoolean("readStatus");

    public final com.ssafy.los.backend.user.model.entity.QUser receiver;

    public final NumberPath<Long> referenceId = createNumber("referenceId", Long.class);

    public QAlert(String variable) {
        this(Alert.class, forVariable(variable), INITS);
    }

    public QAlert(Path<? extends Alert> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlert(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlert(PathMetadata metadata, PathInits inits) {
        this(Alert.class, metadata, inits);
    }

    public QAlert(Class<? extends Alert> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.alertType = inits.isInitialized("alertType") ? new QAlertType(forProperty("alertType")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.ssafy.los.backend.user.model.entity.QUser(forProperty("receiver")) : null;
    }

}

