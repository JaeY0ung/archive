package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.dto.song.SongDto;
import java.util.List;

public interface SongService {

    List<SongDto> searchSongByFilter(String keyword);

    SongDto searchById(Long songId);
}
