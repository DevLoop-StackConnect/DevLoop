package com.devloop.order.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.service.CartService;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private com.devloop.order.entity.Order order;
    private Cart cart;
    private List<CartItem> cartItems;
    private String orderId = "테스트 orderid";

    @BeforeEach
    public void setUp() throws Exception {
        // Product 및 CartItem 생성
        Product product1 = new Product("치킨먹고싶다", BigDecimal.valueOf(20000));
        Product product2 = new Product("피자", BigDecimal.valueOf(23000));

        User user = User.of("조은솔", "test@example.com", "password123!", UserRole.ROLE_USER);

        cart = Cart.of(BigDecimal.valueOf(43000), user);
        CartItem cartItem1 = CartItem.from(cart, product1);
        CartItem cartItem2 = CartItem.from(cart, product2);
        cartItems = Arrays.asList(cartItem1, cartItem2);

        Field itemFeild = Cart.class.getDeclaredField("items");
        itemFeild.setAccessible(true);
        itemFeild.set(cart, cartItems);

        order = com.devloop.order.entity.Order.of(BigDecimal.valueOf(43000),
                orderId,
                "주문이름",
                user,
                cart);

        // ID 설정 (리플렉션 사용)
        Field cartIdField = Cart.class.getDeclaredField("id");
        cartIdField.setAccessible(true);
        cartIdField.set(cart, 1L);

        Field orderIdField = Order.class.getDeclaredField("id");
        orderIdField.setAccessible(true);
        orderIdField.set(order, 1L);
    }

    @Test
    void 유효한_주문아이디로_주문_항목_저장_성공() {
        // given
        when(orderRepository.findByOrderRequestId(orderId)).thenReturn(Optional.of(order));

        // when
        orderItemService.saveOrderItem(orderId);

        // then
        // OrderItem 객체가 Order의 Cart로부터 생성되었는지 검증
        verify(orderItemRepository, Mockito.times(1)).saveAll(anyList());

        // Order 객체의 Cart 참조가 해제되었는지 검증
        assertNull(order.getCart());

        // Cart 삭제가 호출되었는지 검증
        verify(cartService, Mockito.times(1)).deleteCart(cart.getId());
    }
}
