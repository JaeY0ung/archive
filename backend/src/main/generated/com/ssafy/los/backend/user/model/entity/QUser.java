package com.ssafy.los.backend.user.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1870569165L;

    public static final QUser user = new QUser("user");

    public final com.ssafy.los.backend.common.model.entity.QBaseEntity _super = new com.ssafy.los.backend.common.model.entity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> birthDate = createDateTime("birthDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> cash = createNumber("cash", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final StringPath firebaseToken = createString("firebaseToken");

    public final BooleanPath gender = createBoolean("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> multiScore = createNumber("multiScore", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath provider = createString("provider");

    public final StringPath pwdHash = createString("pwdHash");

    public final StringPath role = createString("role");

    public final NumberPath<Integer> singleScore = createNumber("singleScore", Integer.class);

    public final StringPath userImg = createString("userImg");

    public final StringPath uuid = createString("uuid");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

