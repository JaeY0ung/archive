package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Genre;
import java.util.List;

public interface GenreService {

    List<Genre> searchAllGenres();

    Genre selectGenreById(Long genreId);
}
