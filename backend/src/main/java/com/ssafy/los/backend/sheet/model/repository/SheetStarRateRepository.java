package com.ssafy.los.backend.sheet.model.repository;

import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SheetStarRateRepository extends JpaRepository<SheetStarRate, Long> {

    List<SheetStarRate> findAllBySheet(Sheet sheet);
}
