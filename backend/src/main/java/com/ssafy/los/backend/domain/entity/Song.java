package com.ssafy.los.backend.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long id;

    private String title;

    private String composer;

    private String imgName;

    @JoinColumn(name = "genre_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Genre genre;


    @Builder
    public Song(String title, String composer, String imgName, Genre genre) {
        this.title = title;
        this.composer = composer;
        this.imgName = imgName;
        this.genre = genre;
    }
}
