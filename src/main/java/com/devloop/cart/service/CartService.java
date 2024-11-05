package com.devloop.cart.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.repository.CartItemRepository;
import com.devloop.cart.repository.CartRepository;
import com.devloop.cart.response.CartItemListResponse;
import com.devloop.cart.response.CartResponse;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.product.entity.Product;
import com.devloop.product.service.ProductService;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
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
        if (cart == null) {
            Cart newCart = Cart.of(
                    product.getPrice(),
                    user
            );
            cartRepository.save(newCart);
            CartItem cartItem = CartItem.from(newCart, product);
            newCart.addItem(cartItem);
        } else {
            CartItem cartItem = CartItem.from(cart, product);
            if(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).isPresent()){
                throw new ApiException(ErrorStatus._PRODUCT_ALREADY_EXIST);
            }
            cart.addItem(cartItem);

            // 총 가격 업데이트 (기존 장바구니 totalPrice + 상품 price)
            cart.updateTotalPrice(cart.getTotalPrice().add(product.getPrice()));
        }

        return String.format("상품 [ %s ]이 장바구니에 추가 되었습니다.", product.getTitle());
    }

    // 장바구니에 담긴 상품 조회 (다건 조회)
    public CartResponse getAllCartItems(AuthUser authUser, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // Cart 객체 가져오기
        Cart cart = cartRepository.findByUserId(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_CART_ITEM));

        Page<CartItemListResponse> cartItems = cartItemRepository.findAllByCartId(pageable, cart.getId());

        // 값이 비어있을때 예외처리
        if (cartItems.isEmpty()) {
            throw new ApiException(ErrorStatus._NOT_FOUND_CART_ITEM);
        }

        return CartResponse.of(
                user.getUsername(),
                cart.getTotalPrice(),
                cartItems.getTotalElements(),
                cartItems
        );
    }

    // 장바구니에 담긴 상품 삭제
    @Transactional
    public void deleteItemFromCart(AuthUser authUser, Long productId) {
        // Cart 객체 가져오기
        Cart cart = cartRepository.findByUserId(authUser.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_CART_ITEM));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_CART_ITEM));

        cart.deleteItem(cartItem);
        cart.updateTotalPrice(cart.getTotalPrice().subtract(cartItem.getProduct().getPrice()));

        // 장바구니에 담긴 상품이 없으면 장바구니 삭제
        if(cart.getItems().isEmpty()) {
            cartRepository.delete(cart);
        }
    }

    // 장바구니 삭제
    @Transactional
    public void deleteCart(Long cartId){
        // Cart 객체 가져오기
        Cart cart = findById(cartId);
        // Cart 삭제
        cartRepository.delete(cart);
    }

    // Utile method
    public Cart findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_CART));
    }

    public Cart findById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_CART));
    }
}
