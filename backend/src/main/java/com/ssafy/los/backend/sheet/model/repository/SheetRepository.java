package com.ssafy.los.backend.sheet.model.repository;

import com.ssafy.los.backend.sheet.model.entity.Sheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Sheet, Long> {

    List<Sheet> findByDeletedAtIsNullAndCreatedAtIsNotNullOrderByCreatedAtDesc();

//    List<SheetResponseDto> findSheetResponseDtoListByDeletedAtIsNullAndTitleContains(String keyword);
}
