package com.ssafy.los.backend.domain.repository.payment;

import com.ssafy.los.backend.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
