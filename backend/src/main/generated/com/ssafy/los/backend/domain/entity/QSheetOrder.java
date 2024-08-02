package com.ssafy.los.backend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSheetOrder is a Querydsl query type for SheetOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSheetOrder extends EntityPathBase<SheetOrder> {

    private static final long serialVersionUID = 2134388783L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSheetOrder sheetOrder = new QSheetOrder("sheetOrder");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPayment payment;

    public final QSheet sheet;

    public final QUser user;

    public QSheetOrder(String variable) {
        this(SheetOrder.class, forVariable(variable), INITS);
    }

    public QSheetOrder(Path<? extends SheetOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSheetOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSheetOrder(PathMetadata metadata, PathInits inits) {
        this(SheetOrder.class, metadata, inits);
    }

    public QSheetOrder(Class<? extends SheetOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment")) : null;
        this.sheet = inits.isInitialized("sheet") ? new QSheet(forProperty("sheet"), inits.get("sheet")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

