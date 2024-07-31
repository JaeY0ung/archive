package com.ssafy.los.backend.sheet.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class SheetUploadForm {

    private MultipartFile file;

    private String title;

    private Long songId;

    private Integer level; // 1,2,3,4,5,6,7,8,9

    @Builder
    public SheetUploadForm(MultipartFile file, String title, Long songId, Integer level) {
        this.file = file;
        this.title = title;
        this.songId = songId;
        this.level = level;
    }
}
