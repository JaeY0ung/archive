package com.ssafy.los.backend.sheet.model.repository;

import com.ssafy.los.backend.sheet.model.entity.Difficulty;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DifficultyRepository extends JpaRepository<Difficulty, Long> {

    //    List<DifficultyRating> findAllBySheetAndDeletedAtNull(Sheet sheet);
    List<Difficulty> findAllBySheet(Sheet sheet);
}
