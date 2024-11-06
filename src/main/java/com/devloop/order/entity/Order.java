package com.devloop.order.entity;

import com.devloop.cart.entity.Cart;
import com.devloop.common.Timestamped;
import com.devloop.order.enums.OrderStatus;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String orderRequestId;

    @Column(nullable = false)
    private String orderName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "cart_id", nullable = true)
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.WAIT;

    private Order(
            BigDecimal totalPrice,
            String orderRequestId,
            String orderName,
            User user,
            Cart cart
    ) {
        this.totalPrice = totalPrice;
        this.orderRequestId = orderRequestId;
        this.orderName = orderName;
        this.user = user;
        this.cart = cart;
    }

    public static Order of(
            BigDecimal totalPrice,
            String orderRequestId,
            String orderName,
            User user,
            Cart cart
    ) {
        return new Order(
                totalPrice,
                orderRequestId,
                orderName,
                user,
                cart
        );
    }

    // 주문 상태 변경
    public void updateStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    // Cart 와 관계 해재
    public void preRemoveCart() {
        this.cart = null;
    }
}
