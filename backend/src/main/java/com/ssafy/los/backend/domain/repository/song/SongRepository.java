package com.ssafy.los.backend.domain.repository.song;

import com.ssafy.los.backend.domain.entity.Song;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Override
    Optional<Song> findById(Long songId);

    List<Song> findAllByTitleContaining(String keyword);
}
