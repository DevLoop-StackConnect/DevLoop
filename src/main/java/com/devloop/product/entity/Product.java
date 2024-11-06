package com.devloop.product.entity;

import com.devloop.common.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal price;

    public Product(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public void update(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }
}
