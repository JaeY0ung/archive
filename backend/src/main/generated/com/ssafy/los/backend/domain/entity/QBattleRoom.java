package com.ssafy.los.backend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBattleRoom is a Querydsl query type for BattleRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBattleRoom extends EntityPathBase<BattleRoom> {

    private static final long serialVersionUID = 1537776819L;

    public static final QBattleRoom battleRoom = new QBattleRoom("battleRoom");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath title = createString("title");

    public QBattleRoom(String variable) {
        super(BattleRoom.class, forVariable(variable));
    }

    public QBattleRoom(Path<? extends BattleRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBattleRoom(PathMetadata metadata) {
        super(BattleRoom.class, metadata);
    }

}

