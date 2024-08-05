package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.Genre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAll();
}
