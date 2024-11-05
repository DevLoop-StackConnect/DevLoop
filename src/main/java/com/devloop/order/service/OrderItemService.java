package com.devloop.order.service;

import com.devloop.cart.service.CartService;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.order.entity.Order;
import com.devloop.order.entity.OrderItem;
import com.devloop.order.repository.OrderItemRepository;
import com.devloop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
//    private final OrderService orderService;
    private final OrderRepository orderRepository;  // 순환 참조 막기 위해 레파지토리 주입
    private final CartService cartService;

    @Transactional
    public void saveOrderItem(String orderRequestId) {
        // orderRequestId(UUID : orderId)로 Order 객체 찾기
        Order order = orderRepository.findByOrderRequestId(orderRequestId).orElseThrow(()->new ApiException(ErrorStatus._NOT_FOUND_ORDER));

        // OrderItem 객체 생성
        List<OrderItem> orderItems = order.getCart().getItems().stream()
                .map(o->OrderItem.of(order, o.getProduct().getTitle(), o.getProduct().getPrice()))
                .collect(Collectors.toList());

        // OrderItem 객체 저장
        orderItemRepository.saveAll(orderItems);

        Long cartId = order.getCart().getId();
        // Order 객체의 cart 참조 해제
        order.preRemoveCart();
        // 장바구니 삭제
        cartService.deleteCart(cartId);
    }
}
