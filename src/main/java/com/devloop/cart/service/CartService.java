package com.devloop.cart.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.repository.CartRepository;
import com.devloop.common.AuthUser;
import com.devloop.product.entity.Product;
import com.devloop.product.service.ProductService;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    // 장바구니에 상품 아이템 추가
    @Transactional
    public String addItemToCart(AuthUser authUser, Long productId) {

        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // 상품 객체 가져오기
        Product product = productService.findByProductId(productId);

        // Cart 객체 가져오기
        Cart cart = cartRepository.findByUserId(authUser.getId()).orElse(null);

        // Cart가 존재하지 않을 때 : cart 생성 후 item 저장
        if(cart == null) {
            Cart newCart = Cart.of(
                    product.getPrice(),
                    user
            );
            cartRepository.save(newCart);
            CartItem cartItem = CartItem.from(newCart, product);
            newCart.addItem(cartItem);
        }else{
            CartItem cartItem = CartItem.from(cart, product);
            cart.addItem(cartItem);

            // 총 가격 업데이트 (기존 장바구니 totalPrice + 상품 price)
            cart.updateTotalPrice(cart.getTotalPrice().add(product.getPrice()));
        }

        return String.format("상품 [ %s ]이 장바구니에 추가 되었습니다.", product.getTitle());
    }
}
