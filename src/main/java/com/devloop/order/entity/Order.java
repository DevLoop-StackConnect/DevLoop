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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String orderRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private Order(
            BigDecimal totalPrice,
            String orderRequestId,
            User user,
            Cart cart,
            OrderStatus status
    ) {
        this.totalPrice = totalPrice;
        this.orderRequestId = orderRequestId;
        this.user = user;
        this.cart = cart;
        this.status = status;
    }

    public static Order of(
            BigDecimal totalPrice,
            String orderRequestId,
            User user,
            Cart cart,
            OrderStatus status
    ) {
        return new Order(
                totalPrice,
                orderRequestId,
                user,
                cart,
                status
        );
    }


}
