package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.domain.entity.Song;
import java.util.List;

public interface SongService {

    List<Song> searchSongByTitle(String title);

    Song searchById(Long songId);

    List<Song> searchAll();
}
