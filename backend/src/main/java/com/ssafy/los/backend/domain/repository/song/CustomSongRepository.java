package com.ssafy.los.backend.domain.repository.song;

import com.ssafy.los.backend.dto.song.response.SongDto;
import java.util.List;

public interface CustomSongRepository {

    List<SongDto> searchSongsByFilter(String keyword);

    SongDto searchSongDtoById(Long songId);

}
