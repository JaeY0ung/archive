package com.ssafy.los.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// 주문 상품
@Entity
@Getter
@Setter
public class OrderSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_sheet_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id")
    private Sheet sheet;

    public static OrderSheet createOrderSheet(Sheet sheet) {
        OrderSheet orderSheet = new OrderSheet();
        orderSheet.setSheet(sheet);

        return orderSheet;
    }

}
