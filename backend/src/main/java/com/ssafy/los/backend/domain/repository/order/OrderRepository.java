package com.ssafy.los.backend.domain.repository.order;

import com.ssafy.los.backend.domain.entity.Order;
import com.ssafy.los.backend.domain.entity.Sheet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select s from com.ssafy.los.backend.domain.entity.Sheet s where s.id in :sheetIds")
    List<Sheet> findSheetsByIds(@Param("sheetIds") List<Long> sheetIds);

}
