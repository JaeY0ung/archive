package com.ssafy.los.backend.auth_jh.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.los.backend.auth_jh.dto.Authority;
import com.ssafy.los.backend.common.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
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
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(64)", nullable = false)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private Set<Authority> authorities;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "이메일 주소 양식을 확인해주세요")
    private String email;


    private LocalDateTime birthDate;

    private Boolean gender;

    private String token;

    @ColumnDefault("0")
    private int cash;

    private String userImg;

    private Timestamp deletedAt;

    @Builder
    public Customer(String password, String role, Set<Authority> authorities, String email,
            LocalDateTime birthDate, String username, Boolean gender) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
    }
}
