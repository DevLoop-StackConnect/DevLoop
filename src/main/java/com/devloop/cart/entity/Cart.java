package com.devloop.cart.entity;

import com.devloop.common.Timestamped;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private Cart(BigDecimal totalPrice, User user) {
        this.totalPrice = totalPrice;
        this.user = user;
    }

    public static Cart of(BigDecimal totalPrice, User user) {
        return new Cart(totalPrice, user);
    }

    // CartItem을 Cart에 추가하면 CartItem의 Cart 객체를 해당 객체로 저장시켜 양방향 관계 설정함
    public void addItem(CartItem item) {
        items.add(item);
        item.assignCart(this);  // 양방향 관계 설정 메서드
    }

    public void deleteItem(CartItem item) {
        items.remove(item);
    }

    // totaPrice 업데이트
    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}
