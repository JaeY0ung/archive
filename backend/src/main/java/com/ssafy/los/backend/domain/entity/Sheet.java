package com.ssafy.los.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sheet_id")
    private Long id;

    private String title;

    @JoinColumn(name = "song_id")
    @ManyToOne(optional = true)
    private Song song;

    @JoinColumn(name = "uploader_id")
    @ManyToOne
    private User uploader;

    private String uuid;

    private Integer price;

    private Integer level; // 1,2,3,4,5

    // TODO : Default : (0: waiting)
    private Integer status; // "waiting: 0, accepted: 1, rejected: 2"

    @ColumnDefault("0")
    private Integer viewCount;

    @CreatedDate
    private Timestamp createdAt;

    @CreatedDate
    @LastModifiedDate
    private Timestamp modifiedAt;

    private Timestamp deletedAt;


    @Builder
    public Sheet(Long id, Integer level, String title, Song song, Integer price,
            User uploader, String uuid) {
        this.id = id;
        this.uploader = uploader;
        this.level = level;
        this.title = title;
        this.song = song;
        this.price = price;
        this.uuid = uuid;
        this.status = 0;
        this.viewCount = 0;
    }

    public void updateStatus(int status) {
        this.status = status;
    }

    public void updateTitleAndLevel(String title, Integer level) {
        this.title = title;
        this.level = level;
    }

    public void updateLevel(Integer level) {
        this.level = level;
    }
}
