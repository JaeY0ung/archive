package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Sheet, Long>, CustomSheetRepository {
    
}
