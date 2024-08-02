package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.domain.repository.song.SongRepository;
import com.ssafy.los.backend.dto.song.SongDto;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final FileUploadUtil fileUploadUtil;

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

}
