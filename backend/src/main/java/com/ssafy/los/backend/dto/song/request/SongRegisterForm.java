package com.ssafy.los.backend.dto.song.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
public class SongRegisterForm {

    private MultipartFile file;

    private String title;

    private String composer;

    private Long genreId;

    @Builder
    public SongRegisterForm(MultipartFile file, String title, String composer, Long genreId) {
        this.file = file;
        this.title = title;
        this.composer = composer;
        this.genreId = genreId;
    }
}
