package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.song.SongRepository;
import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.dto.song.response.SongDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.GenreService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final FileUploadUtil fileUploadUtil;
    private final AuthService authService;
    private final GenreService genreService;

    @Override
    public List<SongDto> searchSongByFilter(String keyword) {
        return songRepository.searchSongsByFilter(keyword).stream()
                .peek(dto -> dto.loadSongImg(fileUploadUtil))
                .toList();
    }

    @Override
    public SongDto searchById(Long songId) {
        SongDto song = songRepository.searchSongDtoById(songId);
        song.loadSongImg(fileUploadUtil);
        return song;
    }

    @Override
    @Transactional
    public Song registerSongAndFile(SongRegisterForm songRegisterForm)
            throws IllegalArgumentException {
        String uuid = UUID.randomUUID().toString();
        if (songRegisterForm.getFile() != null) {
            saveSongImgFile(songRegisterForm.getFile(), uuid);
            String fileName = uuid + FilenameUtils.getExtension(
                    songRegisterForm.getFile().getOriginalFilename());
            return registerSong(songRegisterForm, fileName);
        }

        return registerSong(songRegisterForm, null);
    }

    @Override
    public boolean deleteById(Long songId) {
        User loginUser = authService.getLoginUser();
        Song song = songRepository.findById(songId).orElse(null);
        if (song != null && loginUser != null && loginUser.getRole().equals("ROLE_ADMIN")) {
            songRepository.deleteById(songId);
            return true;
        }
        return false;
    }

    private void saveSongImgFile(MultipartFile file, String uuid)
            throws IllegalArgumentException {
        fileUploadUtil.uploadSongImg(file, uuid); // 로컬에 저장
    }

    private Song registerSong(SongRegisterForm songRegisterForm, String fileName)
            throws IllegalArgumentException {
        try {
            Song song = Song.builder()
                    .title(songRegisterForm.getTitle())
                    .composer(songRegisterForm.getComposer())
                    .imgName(fileName)
                    .genre(genreService.searchGenreById(songRegisterForm.getGenreId()))
                    .build();
            return songRepository.save(song);
        } catch (Exception e) {
            throw new IllegalArgumentException("곡 저장 실패");
        }
    }

}
