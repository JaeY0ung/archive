package com.ssafy.los.backend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAlertType is a Querydsl query type for AlertType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlertType extends EntityPathBase<AlertType> {

    private static final long serialVersionUID = 876260278L;

    public static final QAlertType alertType = new QAlertType("alertType");

    public final StringPath alertTableName = createString("alertTableName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QAlertType(String variable) {
        super(AlertType.class, forVariable(variable));
    }

    public QAlertType(Path<? extends AlertType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAlertType(PathMetadata metadata) {
        super(AlertType.class, metadata);
    }

}

