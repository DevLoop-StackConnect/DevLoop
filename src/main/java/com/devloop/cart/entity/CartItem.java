package com.devloop.cart.entity;

import com.devloop.common.Timestamped;
import com.devloop.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private CartItem(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
    }

    public static CartItem from(Cart cart, Product product) {
        return new CartItem(cart, product);
    }

    // Cart 객체 설정 메서드
    public void assignCart(Cart cart) {
        this.cart = cart;
    }
}
