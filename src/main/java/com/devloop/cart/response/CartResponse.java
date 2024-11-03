package com.devloop.cart.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

@Getter
public class CartResponse {

    private final String userName;
    private final BigDecimal totalPrice;
    private final Long quantity;
    private final Page<CartItemListResponse> cartItemList;

    private CartResponse(
            String userName,
            BigDecimal totalPrice,
            Long quantity,
            Page<CartItemListResponse> cartItemList
    ) {
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.cartItemList = cartItemList;
    }

    public static CartResponse of(
            String userName,
            BigDecimal totalPrice,
            Long quantity,
            Page<CartItemListResponse> cartItemList
    ){
        return new CartResponse(userName, totalPrice, quantity, cartItemList);
    }

}
