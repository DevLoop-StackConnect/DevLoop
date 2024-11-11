package com.devloop.order.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.service.CartService;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.order.entity.Order;
import com.devloop.order.repository.OrderItemRepository;
import com.devloop.order.repository.OrderRepository;
import com.devloop.product.entity.Product;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderItemService orderItemService;

    private Order order;
    private Cart cart;
    private String orderRequestId;

    @BeforeEach
    void setUp() {
        User user = User.of("홍길동", "test@test.com", "123Ksd!", UserRole.ROLE_USER);
        cart = Cart.of(BigDecimal.TEN, user);

        // 장바구니 항목 추가
        Product product1 = new Product("Product 1", BigDecimal.TEN);
        Product product2 = new Product("Product 2", BigDecimal.TEN);
        CartItem cartItem1 = CartItem.from(cart, product1);
        CartItem cartItem2 = CartItem.from(cart, product2);
        cart.addItem(cartItem1);
        cart.addItem(cartItem2);

        // 주문 객체 생성 및 초기화
        orderRequestId = UUID.randomUUID().toString();
        order = Order.of(BigDecimal.TEN, orderRequestId, "Test Order", user, cart);
    }

    @Test
    void 주문_생성_성공() {
        // given
        when(orderRepository.findByOrderRequestId(orderRequestId)).thenReturn(Optional.of(order));

        // when
        orderItemService.saveOrderItem(orderRequestId);

        // then
        verify(orderItemRepository, times(1)).saveAll(anyList());
        verify(cartService, times(1)).deleteCart(cart.getId());
        verify(orderRepository, times(1)).findByOrderRequestId(orderRequestId);
        assertNull(order.getCart());
    }

    @Test
    void 주문_데이터_없어서_실패() {
        // given
        when(orderRepository.findByOrderRequestId(orderRequestId)).thenReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> orderItemService.saveOrderItem(orderRequestId));
        assertEquals(ErrorStatus._NOT_FOUND_ORDER, exception.getErrorCode());
    }
}
