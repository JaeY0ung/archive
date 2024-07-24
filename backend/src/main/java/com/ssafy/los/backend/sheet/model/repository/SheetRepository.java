package com.ssafy.los.backend.sheet.model.repository;

import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Sheet, Long> {

    List<SheetResponseDto> findSheetResponseDtoListByDeletedAtIsNullAndCreatedAtIsNotNullOrderByCreatedAt();

    List<SheetResponseDto> findSheetResponseDtoListByDeletedAtIsNullAndTitleContains(
            String keyword);
}
