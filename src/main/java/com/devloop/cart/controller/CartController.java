package com.devloop.cart.controller;

import com.devloop.cart.service.CartService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    // 장바구니에 상품 아이템 추가
    @PostMapping("/v2/carts/products/{productId}")
    public ApiResponse<String> addItemToCart(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId
    ) {
        return ApiResponse.ok(cartService.addItemToCart(authUser, productId));
    }

}
