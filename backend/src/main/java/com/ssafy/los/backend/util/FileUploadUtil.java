package com.ssafy.los.backend.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileUploadUtil {

    private final FileValidator fileValidator;

    public FileUploadUtil(FileValidator fileValidator) {
        this.fileValidator = fileValidator;
    }

    @Value("${file.path.user-img}")
    private String userImgFolderPath;

    @Value("${file.path.song-img}")
    private String songImgFolderPath;

    @Value("${file.path.upload-sheet.mid}")
    private String sheetMidFileFolderPath;

    @Value("${file.path.upload-sheet.musicxml}")
    private String sheetMusicXmlFileFolderPath;

//    @Value("${file.path.play-record}")
//    private String playRecordFolderPath;

    public void uploadUserImg(MultipartFile file, String uuid) throws IllegalArgumentException {
        fileValidator.validateImageFile(getExtension(file));
        saveFileByUuid(userImgFolderPath, file, uuid);
    }

    public void uploadSongImg(MultipartFile file, String uuid) throws IllegalArgumentException {
        fileValidator.validateImageFile(getExtension(file));
        saveFileByUuid(songImgFolderPath, file, uuid);
    }

    public void uploadSheet(MultipartFile file, String uuid) throws IllegalArgumentException {
        fileValidator.validateMidFile(getExtension(file));
        saveFileByUuid(sheetMidFileFolderPath, file, uuid);
    }

    public String getSongImgByFileName(String fileName) {
        Path path = getSongImgPathByFileName(fileName);
        return getFileAsBase64(path);
    }

    public String getMusicXmlByUuid(String uuid) {
        Path path = getMusicXmlPathByFileName(
                uuid + ".musicxml");
        return readFileAsString(path);
    }

    public String getMidByUuid(String uuid) {
        Path path = getMidPathByFileName(uuid + ".mid");
        return readFileAsString(path);
    }

//    public void uploadPlayRecord(MultipartFile file, String uuid)
//            throws IllegalArgumentException {
//        fileValidator.validateWebmFile(getExtension(file));
//        saveFile(playRecordFolderPath, file, uuid);
//    }

    public Resource downloadSheetFile(String uuid) throws IllegalArgumentException {
        return downloadOneFile(sheetMidFileFolderPath, uuid + ".mid");
    }


    private Path getSongImgPathByFileName(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("악보에 연결된 곡의 이미지 파일이 없습니다");
        }
        return getPathByFileName(songImgFolderPath, fileName);
    }

    private Path getMusicXmlPathByFileName(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("악보에 연결된 곡의 musicXml 파일이 없습니다");
        }
        return getPathByFileName(sheetMusicXmlFileFolderPath, fileName);
    }

    private Path getMidPathByFileName(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("악보에 연결된 곡의 mid 파일이 없습니다");
        }
        return getPathByFileName(sheetMidFileFolderPath, fileName);
    }

    public String getUserImg(String imgName) {
        Path path = getSomgUserImgPath(imgName);
        return getFileAsBase64(path);
    }

    private Path getSomgUserImgPath(String fileName) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("유저에 연결된 곡의 이미지 파일이 없습니다");
        }
        return getPathByFileName(userImgFolderPath, fileName);
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

    private void saveFileByUuid(String folderPath, MultipartFile file, String uuid)
            throws IllegalArgumentException {
        makeDirectoryIfNotExists(folderPath);
        String saveFileName = uuid + "." + getExtension(file);

        try {
            file.transferTo(new File(folderPath, saveFileName));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not save file " + saveFileName, e);
        }

    }

    private Resource downloadOneFile(String folderPath, String fileName)
            throws IllegalArgumentException {
        try {
            UrlResource resource = new UrlResource(
                    getPathByFileName(folderPath, fileName).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            return null;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("파일을 가져올 수 없습니다.", e);
        }
    }

    private Path getPathByFileName(String folderPath, String fileName) {
        return Paths.get(folderPath, fileName);
    }

    private void makeDirectoryIfNotExists(String folderPath) throws IllegalArgumentException {
        File directory = new File(folderPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalArgumentException("파일 저장 디렉토리 생성 실패");
        }
    }

    public String getExtension(MultipartFile file) throws IllegalArgumentException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일의 이름이 없습니다."); // NO_FILE_NAME_MESSAGE
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }

}
