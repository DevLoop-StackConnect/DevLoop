package com.devloop.order.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.service.CartService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.order.entity.Order;
import com.devloop.order.repository.OrderRepository;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;

    // 주문 하기 (주문 객체 생성)
    @Transactional
    public Order createOrder(AuthUser authUser) {

        // 사용자 객체 가져오기
        User user = userService.findByUserId(authUser.getId());

        // 장바구니 객체 가져오기
        Cart cart = cartService.findByUserId(authUser.getId());

        // Order 객체 생성
        Order order = Order.of(
                cart.getTotalPrice(),
                UUID.randomUUID().toString(),
                String.format("%s 외 %d 개", cart.getItems().get(0).getProduct().getTitle(), cart.getItems().size()),
                user,
                cart
        );
        orderRepository.save(order);

        return order;
    }


    public Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_ORDER));
    }
}
