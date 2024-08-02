package com.ssafy.los.backend.domain.repository.song;

import com.ssafy.los.backend.domain.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long>, CustomSongRepository {

}
