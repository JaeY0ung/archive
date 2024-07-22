package com.ssafy.los.backend.sheet.model.repository;

import com.ssafy.los.backend.sheet.model.entity.DifficultyRating;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DifficultyRatingRepository extends JpaRepository<DifficultyRating, Long> {

    //    List<DifficultyRating> findAllBySheetAndDeletedAtNull(Sheet sheet);
    List<DifficultyRating> findAllBySheet(Sheet sheet);
}
