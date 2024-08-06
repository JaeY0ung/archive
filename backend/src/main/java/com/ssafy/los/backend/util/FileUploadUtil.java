package com.ssafy.los.backend.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
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
    private String userImgFolderPath;

    @Value("${file.path.song-img}")
    private String songImgFolderPath;

    @Value("${file.path.upload-sheet.mid}")
    private String sheetMidFileFolderPath;

    @Value("${file.path.upload-sheet.musicxml}")
    private String sheetMusicXmlFileFolderPath;

    @Value("${file.path.play-record}")
    private String playRecordFolderPath;

    public String uploadUserImg(MultipartFile file) throws IllegalArgumentException {
        validateImageFile(file);
        return saveFile(userImgFolderPath, file);
    }

    public String uploadSongImg(MultipartFile file) throws IllegalArgumentException {
        validateImageFile(file);
        return saveFile(songImgFolderPath, file);
    }

    public String uploadSheet(MultipartFile file) throws IllegalArgumentException {
        validateMidFile(file);
        return saveFile(sheetMidFileFolderPath, file);
    }

    public String uploadPlayRecord(MultipartFile file)
            throws IllegalArgumentException {
        validateWebmFile(file);
        return saveFile(playRecordFolderPath, file);
    }

    public Resource downloadSheetFile(String fileName) throws IllegalArgumentException {
        return downloadOneFile(sheetMidFileFolderPath, fileName);
    }

    public String getSongImg(String imgName) {
        Path path = getSongImgPath(imgName);
        return getFileAsBase64(path);
    }

    // TODO : 바꾸기
    public String getMusicXml(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, dotIndex) + ".musicxml";
        log.info(fileName);
        Path path = getMusicXmlPath(fileName);
        return readFileAsString(path);
    }

    // TODO : 바꾸기
    public String getMid(String fileName) {
        Path path = getMusicXmlPath(fileName);
        return readFileAsString(path);
    }

    private Path getSongImgPath(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("악보에 연결된 곡의 이미지 파일이 없습니다");
        }
        return getPath(songImgFolderPath, fileName);
    }

    private Path getMusicXmlPath(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("악보에 연결된 곡의 이미지 파일이 없습니다");
        }
        return getPath(sheetMusicXmlFileFolderPath, fileName);
    }

    public String getUserImg(String imgName) {
        Path path = getSomgUserImgPath(imgName);
        return getFileAsBase64(path);
    }

    private Path getSomgUserImgPath(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("유저에 연결된 곡의 이미지 파일이 없습니다");
        }
        return getPath(userImgFolderPath, fileName);
    }

    public String getFileAsBase64(Path path) {
        try {
            byte[] songImg = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(songImg);
        } catch (IOException e) {
            return null;
        }
    }

    public String readFileAsString(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다: " + path, e);
        }
    }

    private String saveFile(String folderPath, MultipartFile file)
            throws IllegalArgumentException {
        makeDirectoryIfNotExists(folderPath);
        String saveFileName = UUID.randomUUID() + "." + getExtension(file);

        try {
            file.transferTo(new File(folderPath, saveFileName));
            return saveFileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not save file " + saveFileName, e);
        }

    }

    private Resource downloadOneFile(String folderPath, String fileName)
            throws IllegalArgumentException {
        try {
            UrlResource resource = new UrlResource(getPath(folderPath, fileName).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            return null;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("파일을 가져올 수 없습니다.", e);
        }
    }

    private Path getPath(String folderPath, String fileName) {
        return Paths.get(folderPath, fileName);
    }

    private void makeDirectoryIfNotExists(String folderPath) throws IllegalArgumentException {
        File directory = new File(folderPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalArgumentException("파일 저장 디렉토리 생성 실패");
        }
    }

    private String getExtension(MultipartFile file) throws IllegalArgumentException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일의 이름이 없습니다."); // NO_FILE_NAME_MESSAGE
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }

    private void validateImageFile(MultipartFile file) throws IllegalArgumentException {
        String ext = getExtension(file);
        if (!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
            throw new IllegalArgumentException("파일의 확장자가 png, jpg, jpeg 증 히나가 아닙니다.");
        }
    }

    private void validateMidFile(MultipartFile file) throws IllegalArgumentException {
        String ext = getExtension(file);
        if (!ext.equals("mid")) {
            throw new IllegalArgumentException("파일의 확장자가 mid 가 아닙니다.");
        }
    }

    private void validateWebmFile(MultipartFile file) throws IllegalArgumentException {
        String ext = getExtension(file);
        if (!ext.equals("webm")) {
            throw new IllegalArgumentException("파일의 확장자가 webm 가 아닙니다.");
        }
    }
}
