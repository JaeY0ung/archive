package com.ssafy.los.backend.domain.entity;

import com.ssafy.los.backend.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Table(name = "orders")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderSheet> orderSheetList = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태

    @CreatedDate
    private LocalDateTime orderDate; // 주문 시간

    private String tid;

    // == 연관관계 편의 메서드 == //
    // 연관관계 메서드는 Controller 하는 부분에서 들고 있는게 좋다.
    // 미리 메서드의 연관관계를 묶어줌으로써 연관 관계 관련해서 놓치는 부분을 해결할 수 있다.
    public void setUser(User user) {
        this.user = user;
        user.getOrders().add(this);
    }
    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
    public void addOrderSheet(OrderSheet orderSheet) {
        orderSheetList.add(orderSheet);
        orderSheet.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(User user, List<OrderSheet> orderSheets) {
        Order order = new Order();
        order.setUser(user);

        for (OrderSheet orderSheet : orderSheets) {
            order.addOrderSheet(orderSheet);
        }
        order.setStatus(OrderStatus.ORDER);
        return order;
    }

    //==조회 로직==//
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderSheet orderSheet : orderSheetList) {
            totalPrice += orderSheet.getSheet().getPrice();
        }
        return totalPrice;
    }


}
