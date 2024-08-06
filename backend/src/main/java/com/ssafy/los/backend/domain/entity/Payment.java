package com.ssafy.los.backend.domain.entity;

import com.ssafy.los.backend.constant.PayType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PayType name; // 카카오페이, 토스

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
