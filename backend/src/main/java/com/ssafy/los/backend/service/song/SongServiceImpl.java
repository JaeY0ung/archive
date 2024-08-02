package com.ssafy.los.backend.service.song;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.domain.repository.song.SongRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Override
    public List<Song> searchSongByTitle(String title) {
        return songRepository.findAllByTitleContaining(title);
    }

    @Override
    public Song searchById(Long songId) {
        return songRepository.findById(songId).orElse(null);
    }

    @Override
    public List<Song> searchAll() {
        return songRepository.findAll();
    }
}
