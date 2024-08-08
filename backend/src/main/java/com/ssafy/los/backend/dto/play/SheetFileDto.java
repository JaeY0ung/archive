package com.ssafy.los.backend.dto.play;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;

@Getter
@NoArgsConstructor
public class SheetFileDto {

    private File file;
    private int sheetId;

    @Builder
    public SheetFileDto(File file, int sheetId) {
        this.file = file;
        this.sheetId = sheetId;
    }

}
