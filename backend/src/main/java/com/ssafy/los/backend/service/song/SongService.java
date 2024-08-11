package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.dto.song.request.SongUpdateForm;
import com.ssafy.los.backend.dto.song.response.SongDto;
import java.util.List;

public interface SongService {

    List<SongDto> searchSongByFilter(String keyword);

    SongDto searchById(Long songId);

    Song registerSongAndFile(SongRegisterForm songRegisterForm)
            throws IllegalArgumentException;

    Song updateSongAndFile(Long songId, SongUpdateForm songUpdateForm);

    boolean deleteById(Long songId);

}
