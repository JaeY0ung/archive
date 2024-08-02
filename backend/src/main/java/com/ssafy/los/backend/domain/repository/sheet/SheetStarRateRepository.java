package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SheetStarRate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SheetStarRateRepository extends JpaRepository<SheetStarRate, Long> {

    List<SheetStarRate> findAllBySheet(Sheet sheet);
}
