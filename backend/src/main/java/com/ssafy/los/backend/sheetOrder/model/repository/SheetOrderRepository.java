package com.ssafy.los.backend.sheetOrder.model.repository;

import com.ssafy.los.backend.sheetOrder.model.entity.SheetOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetOrderRepository extends JpaRepository<SheetOrder, Long> {

}
