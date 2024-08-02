package com.ssafy.los.backend.dto.song;

import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SongDto {

    private Long id;

    private String title;

    private String composer;

    private String imgName;

    private String genreTitle;

    private String img;

    public void loadSongImg(FileUploadUtil fileUploadUtil) {
        if (this.imgName != null) {
            this.img = fileUploadUtil.getImg(this.imgName);
        }
    }

    public SongDto(Long id, String title, String composer, String imgName, String genreTitle) {
        this.id = id;
        this.title = title;
        this.composer = composer;
        this.imgName = imgName;
        this.genreTitle = genreTitle;
    }
}
