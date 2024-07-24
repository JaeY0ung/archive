package com.ssafy.los.backend.util;

import java.io.File;
import java.io.IOException;
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

    @Value("${file.path.upload-sheet}")
    private String sheetFilePath;

    @Value("${file.path.play-record}")
    private String playRecordPath;

    public String uploadUserImg(MultipartFile file) throws IOException {
        // TODO : png,jpg만 업로드 가능하게 하기
        return saveOneFile(file, userImgPath);
    }

    public String uploadSongImg(MultipartFile file) throws IOException {
        // TODO : png,jpg만 업로드 가능하게 하기
        return saveOneFile(file, songImgPath);
    }

    public String uploadSheet(MultipartFile file) throws IOException {
        // TODO : mid, mp3만 업로드 가능하게 하기
        return saveOneFile(file, sheetFilePath);
    }

    public Resource downloadSheet(String fileName) throws IOException {
        return downloadOneFile(sheetFilePath, fileName);
    }

    public String uploadPlayRecord(MultipartFile file) throws IOException {
        return saveOneFile(file, playRecordPath);
    }


    private void saveFiles(List<MultipartFile> files, String filePath) throws IOException {
        for (MultipartFile file : files) {
            saveOneFile(file, filePath);
        }
    }

    private String saveOneFile(MultipartFile file, String filePath) throws IOException {
        String originalFilename = file.getOriginalFilename(); // 원본 파일명, 저장 될 파일명 생성
        validatePath(filePath);

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일의 이름이 없습니다."); // NO_FILE_NAME_MESSAGE
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + "." + ext;

        File saveFile = new File(filePath, saveFileName);
        file.transferTo(saveFile);
        return saveFileName;
    }

    private Resource downloadOneFile(String filePath, String fileName) throws IOException {
        Path path = Paths.get(filePath, fileName);

        UrlResource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        throw new IllegalArgumentException("파일을 가져올 수 없습니다.");
    }

    private void validatePath(String filePath) throws IOException {
        File directory = new File(filePath);
        if (directory.exists() || directory.mkdirs()) {
            return;
        }
        log.error("파일 저장 디렉토리 생성 실패: " + filePath);
        throw new IOException("파일 저장 디렉토리 생성 실패");
    }
}
