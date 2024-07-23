package com.ssafy.los.backend.user.model.entity;

import com.ssafy.los.backend.common.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private String uuid;

    private String role;

    @Column(columnDefinition = "VARCHAR(64)")
    private String pwdHash;

    @NotNull
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "이메일 주소 양식을 확인해주세요")
    private String email;

    private LocalDateTime birthDate;

    @Column(columnDefinition = "VARCHAR(64)")
    private String nickname;

    private Boolean gender;

    private String token;

    @ColumnDefault("0")
    private int cash;

    private String userImg;

    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
    }

    public void update(String email, String userImg) {
        this.email = email;
        this.userImg = userImg;
    }

    @Builder
    public User(String email, String pwdHash, LocalDateTime birthDate, String nickname,
            Boolean gender,
            String userImg, String role) {
        this.email = email;
        this.pwdHash = pwdHash;
        this.birthDate = birthDate;
        this.nickname = nickname;
        this.gender = gender;
        this.userImg = userImg;
        this.role = role;
    }

    @Builder
    public User(String userImg, String email) {
        this.userImg = userImg;
        this.email = email;
    }
}
