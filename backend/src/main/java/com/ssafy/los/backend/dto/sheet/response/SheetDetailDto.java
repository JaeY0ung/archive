package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.util.FileUploadUtil;

public interface SheetDetailDto {

    void loadSongImg(FileUploadUtil fileUploadUtil);

    String getFileName();

    String getTitle();
}
