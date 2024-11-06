package com.devloop.payment.entity;

import com.devloop.common.Timestamped;
import com.devloop.order.entity.Order;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderRequestId;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    private Payment(
            User user,
            Order order,
            BigDecimal amount,
            String method,
            String paymentKey,
            String orderRequestId,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt
    ) {
        this.user = user;
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.paymentKey = paymentKey;
        this.orderRequestId = orderRequestId;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static Payment of(
            User user,
            Order order,
            BigDecimal amount,
            String method,
            String paymentKey,
            String orderRequestId,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt
    ) {
        return new Payment(
                user,
                order,
                amount,
                method,
                paymentKey,
                orderRequestId,
                requestedAt,
                approvedAt
        );
    }

}
