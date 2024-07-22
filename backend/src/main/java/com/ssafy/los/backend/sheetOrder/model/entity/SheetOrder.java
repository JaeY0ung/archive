package com.ssafy.los.backend.sheetOrder.model.entity;

import com.ssafy.los.backend.payment.model.entity.Payment;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SheetOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sheet_order_id")
    private Long id;


    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User user;

    @JoinColumn(name = "sheet_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Sheet sheet;

    @JoinColumn(name = "payment_id")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Payment payment;

    @CreatedDate
    private Timestamp createdAt;
}
