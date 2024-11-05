package com.devloop.cart.controller;

import com.devloop.cart.response.CartResponse;
import com.devloop.cart.service.CartService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 장바구니에 담긴 상품 조회 (다건 조회)
    @GetMapping("/v2/carts")
    public ApiResponse<CartResponse> getAllCartItems(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(cartService.getAllCartItems(authUser, page, size));
    }

    // 장바구니에 담긴 상품 삭제
    @DeleteMapping("/v2/carts/products/{productId}")
    public ResponseEntity<Void> deleteItemFromCart(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("productId") Long productId
    ) {
        cartService.deleteItemFromCart(authUser, productId);
        return ResponseEntity.noContent().build();
    }

}
