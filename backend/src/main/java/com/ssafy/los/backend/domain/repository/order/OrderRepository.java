package com.ssafy.los.backend.domain.repository.order;

import com.ssafy.los.backend.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTid(String tid);

}
