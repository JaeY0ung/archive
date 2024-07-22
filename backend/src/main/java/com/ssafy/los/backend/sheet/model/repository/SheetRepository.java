package com.ssafy.los.backend.sheet.model.repository;

import com.ssafy.los.backend.sheet.model.entity.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Sheet, Long> {

}
