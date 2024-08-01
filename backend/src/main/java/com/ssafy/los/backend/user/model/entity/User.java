package com.ssafy.los.backend.user.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Column(columnDefinition = "VARCHAR(64)")
    private String nickname;

    private String userImg;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime birthDate;

    private Boolean gender;

    private String provider;

    @ColumnDefault("0")
    private int cash;

    @ColumnDefault("0")
    private Integer singleScore;

    @ColumnDefault("0")
    private Integer multiScore;

    private LocalDateTime deletedAt;

    private String firebaseToken;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Builder
    public User(String email, String pwdHash, LocalDateTime birthDate, String nickname,
            Boolean gender,
            String userImg, String role, Integer singleScore, Integer multiScore, String provider,
            String firebaseToken) {
        this.email = email;
        this.pwdHash = pwdHash;
        this.birthDate = birthDate;
        this.nickname = nickname;
        this.gender = gender;
        this.userImg = userImg;
        this.role = role;
        this.singleScore = singleScore;
        this.multiScore = multiScore;
        this.provider = provider;
        this.firebaseToken = firebaseToken;
    }

//    @Builder
//    public User(String userImg, String email) {
//        this.userImg = userImg;
//        this.email = email;
//    }

    //=== 메서드 ===//
    public void updateProfile(String nickname, String uuid) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (uuid != null) {
            this.uuid = uuid;
        }
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    //=== OAuth2 관련 ===///
    public void OAuth2Update(User TempOAuthUser) {
        this.role = TempOAuthUser.getRole();
        this.nickname = TempOAuthUser.getNickname();
        this.pwdHash = TempOAuthUser.getPwdHash();
        this.gender = TempOAuthUser.getGender();
        this.birthDate = TempOAuthUser.getBirthDate();
    }

    //=== 유저 스코어 관련 ===//
    public void increaseSingleScore(int amount) {
        this.singleScore += amount;
    }

//    public void decreaseSingleScore(int amount) {
//        this.SingleScore -= amount;
//    }

    public void increaseMultiScore(int amount) {
        this.multiScore += amount;
    }

    public void decreaseMultiScore(int amount) {
        this.multiScore -= amount;
    }

}
