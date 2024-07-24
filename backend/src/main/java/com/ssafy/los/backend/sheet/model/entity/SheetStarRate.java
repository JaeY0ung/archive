package com.ssafy.los.backend.sheet.model.entity;

import com.ssafy.los.backend.common.model.entity.BaseEntity;
import com.ssafy.los.backend.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sheet_star_rate")
public class SheetStarRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sheet_star_rate_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id")
    private Sheet sheet;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int starRate;

    @Builder
    public SheetStarRate(User user, Sheet sheet, String content, int starRate) {
        this.user = user;
        this.sheet = sheet;
        this.content = content;
        this.starRate = starRate;
    }

    //=== 메서드 ===//
    public void update(String content, int starRate) {
        this.content = content;
        this.starRate = starRate;
    }

}
