package com.devloop.cart.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CartItemListResponse {

    private final String title;
    private final BigDecimal price;

    private CartItemListResponse(
            String title,
            BigDecimal price
    ) {
        this.title = title;
        this.price = price;
    }

    public static CartItemListResponse of(
            String title,
            BigDecimal price
    ) {
        return new CartItemListResponse(title, price);
    }
}
