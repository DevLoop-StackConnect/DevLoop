package com.devloop.stock.entity;

import com.devloop.common.Timestamped;
import com.devloop.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    private Stock(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public static Stock of(Product product, int quantity) {
        return new Stock(product, quantity);
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity - 1;
    }
}
