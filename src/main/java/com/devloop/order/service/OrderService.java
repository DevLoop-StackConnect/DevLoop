package com.devloop.order.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.service.CartService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.order.entity.Order;
import com.devloop.order.enums.OrderStatus;
import com.devloop.order.repository.OrderRepository;
import com.devloop.product.entity.Product;
import com.devloop.stock.service.StockService;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final StockService stockService;

    // 주문 하기 (주문 요청, 주문 객체 생성)
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

    // 주문 요청됨
    @Transactional
    public void orderRequested(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = findByOrderRequestId(orderRequestId);

        // 주문 상태 "REQUESTED("주문 요청됨")"으로 변경
        order.updateStatus(OrderStatus.REQUESTED);
    }

    // 주문 실패됨(주문 취소)
    @Transactional
    public void orderFailed(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = findByOrderRequestId(orderRequestId);

        // 주문 삭제
        orderRepository.delete(order);
    }

    // 주문 완료됨 (주문 승인됨)
    @Transactional
    public void orderApproved(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = findByOrderRequestId(orderRequestId);

        // 주문 상태 "APPROVED("주문 승인됨")"으로 변경
        order.updateStatus(OrderStatus.APPROVED);

        List<CartItem> cartItems = order.getCart().getItems();
        Product product = (Product) Hibernate.unproxy(cartItems.get(0).getProduct());
        if (product.getClass().getSimpleName().equals("ProjectWithTutor")) {
            // 각 PWT의 Stock 업데이트
            for (CartItem cartItem : cartItems) {
                stockService.updateStock(cartItem.getProduct().getId());
            }
        }

        // 주문 항목 저장
        orderItemService.saveOrderItem(orderRequestId);
    }

    // Utile Method
    public Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_ORDER));
    }

    public Order findByOrderRequestId(String orderRequestId) {
        return orderRepository.findByOrderRequestId(orderRequestId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_ORDER));
    }
}
