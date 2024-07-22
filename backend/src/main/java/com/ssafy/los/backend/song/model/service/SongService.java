package com.ssafy.los.backend.song.model.service;

import com.ssafy.los.backend.song.model.entity.Song;
import java.util.List;

public interface SongService {

    List<Song> searchSongByTitle(String title);

    Song searchById(Long songId);

    List<Song> searchAll();
}
