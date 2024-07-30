package com.ssafy.los.backend.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileUploadUtil {

    @Value("${file.path.user-img}")
    private String userImgPath;

    @Value("${file.path.song-img}")
    private String songImgPath;

    @Value("${file.path.upload-sheet.mid}")
    private String sheetMidFilePath;

    @Value("${file.path.upload-sheet.musicxml}")
    private String sheetMusicXmlFilePath;

    @Value("${file.path.play-record}")
    private String playRecordPath;

    public String uploadUserImg(MultipartFile file) throws IOException {
        String ext = getExtension(file);
        if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
            throw new IllegalArgumentException("파일의 확장자가 png, jpg, jpeg 증 히나가 아닙니다.");
        }
        return saveOneFile(file, userImgPath);
    }

    public String uploadSongImg(MultipartFile file) throws IOException {
        String ext = getExtension(file);
        if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
            throw new IllegalArgumentException("파일의 확장자가 png, jpg, jpeg 증 히나가 아닙니다.");
        }
        return saveOneFile(file, songImgPath);
    }

    public String uploadSheet(MultipartFile file) throws IOException {
        String ext = getExtension(file);
        if (!ext.equals("mid")) {
            throw new IllegalArgumentException("파일의 확장자가 mid 이 아닙니다.");
        }
        // webm
        return saveOneFile(file, sheetMidFilePath);
    }

    public String uploadPlayRecord(MultipartFile file) throws IOException {
        String ext = getExtension(file);
        if (!ext.equals("webm")) {
            throw new IllegalArgumentException("파일의 확장자가 webm 이 아닙니다.");
        }
        return saveOneFile(file, playRecordPath);
    }

    public Resource downloadSheetFile(String fileName) throws IOException {
        return downloadOneFile(sheetMidFilePath, fileName);
    }

    public Path getSomgImgPath(String fileName) {
        return getPath(songImgPath, fileName);
    }

    private void saveFiles(List<MultipartFile> files, String filePath) throws IOException {
        for (MultipartFile file : files) {
            saveOneFile(file, filePath);
        }
    }

    private String saveOneFile(MultipartFile file, String filePath) throws IOException {
        String originalFilename = file.getOriginalFilename(); // 원본 파일명, 저장 될 파일명 생성
        validatePath(filePath);
        String saveFileName = UUID.randomUUID().toString() + "." + getExtension(file);

        File saveFile = new File(filePath, saveFileName);
        file.transferTo(saveFile);
        return saveFileName;
    }

    private Resource downloadOneFile(String filePath, String fileName) {
        Path path = getPath(filePath, fileName);

        UrlResource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("파일을 가져올 수 없습니다.");
        }

        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        return null;
    }

    private Path getPath(String filePath, String fileName) {

        return Paths.get(filePath, fileName);
    }

    private void validatePath(String filePath) throws IOException {
        File directory = new File(filePath);
        if (directory.exists() || directory.mkdirs()) {
            return;
        }
        log.error("파일 저장 디렉토리 생성 실패: " + filePath);
        throw new IOException("파일 저장 디렉토리 생성 실패");
    }

    private String getExtension(MultipartFile file) throws IllegalArgumentException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일의 이름이 없습니다."); // NO_FILE_NAME_MESSAGE
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
