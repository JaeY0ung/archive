package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.domain.repository.song.SongRepository;
import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.dto.song.response.SongDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.GenreService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public Song registerSongAndFile(SongRegisterForm songRegisterForm)
            throws IllegalArgumentException {
        return registerSong(songRegisterForm, saveSongImgFile(songRegisterForm.getFile()));
    }

    private String saveSongImgFile(MultipartFile file) throws IllegalArgumentException {
        return fileUploadUtil.uploadSongImg(file); // 로컬에 저장
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
