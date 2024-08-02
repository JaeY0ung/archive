package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.Difficulty;
import com.ssafy.los.backend.domain.entity.Sheet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DifficultyRepository extends JpaRepository<Difficulty, Long> {

    List<Difficulty> findAllBySheet(Sheet sheet);

    Page<Difficulty> findAllBySheetOrderByCreatedAtDesc(Sheet sheet, Pageable pageable);
}