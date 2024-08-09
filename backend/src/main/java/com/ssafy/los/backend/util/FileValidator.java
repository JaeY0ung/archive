package com.ssafy.los.backend.util;

import org.springframework.stereotype.Component;

@Component
public class FileValidator {

    public void validateImageFile(String ext) throws IllegalArgumentException {
        if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
            throw new IllegalArgumentException("파일의 확장자가 png, jpg, jpeg 증 히나가 아닙니다.");
        }
    }

    public void validateMidFile(String ext) throws IllegalArgumentException {
        if (!ext.equals("mid")) {
            throw new IllegalArgumentException("파일의 확장자가 mid 가 아닙니다.");
        }
    }

    public void validateWebmFile(String ext) throws IllegalArgumentException {
        if (!ext.equals("webm")) {
            throw new IllegalArgumentException("파일의 확장자가 webm 가 아닙니다.");
        }
    }
}
