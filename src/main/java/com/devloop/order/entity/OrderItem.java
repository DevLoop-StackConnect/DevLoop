package com.devloop.order.entity;

import com.devloop.product.entity.Product;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order Order;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    private OrderItem(
            Order order,
            String productName,
            BigDecimal price
    ) {
        this.Order = order;
        this.productName = productName;
        this.price = price;
    }

    public static OrderItem of(
            Order order,
            String productName,
            BigDecimal price
    ) {
        return new OrderItem(order, productName, price);
    }
}
