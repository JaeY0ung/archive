package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Genre;
import com.ssafy.los.backend.domain.repository.sheet.GenreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> searchAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre selectGenreById(Long genreId) {
        return genreRepository.findById(genreId).orElse(null);
    }
}
