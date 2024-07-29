package com.ssafy.los.backend.like.model.entity;

import com.ssafy.los.backend.common.model.entity.BaseEntity;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LikeSheet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_sheet_id")
    private Long id;


    @JoinColumn(name = "user_id")
//    @ManyToOne(cascade = CascadeType.REMOVE)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "sheet_id")
//    @ManyToOne(cascade = CascadeType.REMOVE)
    @ManyToOne(fetch = FetchType.LAZY)
    private Sheet sheet;

    @Builder
    public LikeSheet(User user, Sheet sheet) {
        this.user = user;
        this.sheet = sheet;
    }
}
