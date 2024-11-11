package com.devloop.order.service;

import com.devloop.cart.entity.Cart;
import com.devloop.cart.entity.CartItem;
import com.devloop.cart.service.CartService;
import com.devloop.common.AuthUser;
import com.devloop.common.exception.ApiException;
import com.devloop.order.entity.Order;
import com.devloop.order.enums.OrderStatus;
import com.devloop.order.repository.OrderRepository;
import com.devloop.product.entity.Product;
import com.devloop.purchase.repository.PurchaseRepository;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.stock.entity.Stock;
import com.devloop.stock.repository.StockRepository;
import com.devloop.stock.service.StockService;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private UserService userService;
    @Mock
    private CartService cartService;
    @Mock
    private OrderItemService orderItemService;
    @Mock
    private StockService stockService;
    @Mock
    private ProjectWithTutorService projectWithTutorService;
    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Order order;
    private List<CartItem> cartItems;
    private String orderId = "테스트 orderId";

    @BeforeEach
    public void setUp() throws Exception {
        user = User.of("조은솔", "a@mail.com", "password123!", UserRole.ROLE_USER);
        cart = Cart.of(BigDecimal.valueOf(50000), user);
        Product product = new Product("야채곱창", BigDecimal.valueOf(13000));
        CartItem cartItem = CartItem.from(cart, product);
        cartItems = List.of(cartItem);

        Field itemsField = Cart.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        itemsField.set(cart, cartItems);

        order = Order.of(
                cart.getTotalPrice(),
                orderId,
                "주문이름",
                user,
                cart
        );
    }

    @Test
    void 정상_주문_생성() throws Exception {
        // given
        when(userService.findByUserId(user.getId())).thenReturn(user);
        when(cartService.findByUserId(user.getId())).thenReturn(cart);
        when(purchaseRepository.existsByUserIdAndProductId(user.getId(), cartItems.get(0).getProduct().getId())).thenReturn(false);

        // LazyInitializer Mock 설정
        LazyInitializer lazyInitializer = Mockito.mock(LazyInitializer.class);
        when(lazyInitializer.getImplementation()).thenReturn(cartItems.get(0).getProduct());

        // Product를 HibernateProxy처럼 동작하도록 Mock 설정
        Product product = Mockito.mock(Product.class, withSettings().extraInterfaces(HibernateProxy.class));
        when(((HibernateProxy) product).getHibernateLazyInitializer()).thenReturn(lazyInitializer);

        // 리플렉션을 사용해 CartItem의 product 필드에 Mock Product 설정
        Field productField = CartItem.class.getDeclaredField("product");
        productField.setAccessible(true);
        productField.set(cartItems.get(0), product);

        Stock stock = Stock.of(cartItems.get(0).getProduct(), 10);
        // when
        Order createdOrder = orderService.createOrder(new AuthUser(user.getId(), user.getEmail(), user.getUserRole()));

        // then
        assertNotNull(createdOrder);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void 재구매_예외처리() throws Exception {
        // given
        when(userService.findByUserId(user.getId())).thenReturn(user);
        when(cartService.findByUserId(user.getId())).thenReturn(cart);

        // Product를 HibernateProxy처럼 동작하도록 Mock 설정
        Product product = Mockito.mock(Product.class, withSettings().extraInterfaces(HibernateProxy.class));
        when(product.getId()).thenReturn(1L);

        // LazyInitializer Mock 설정
        LazyInitializer lazyInitializer = Mockito.mock(LazyInitializer.class);
        when(lazyInitializer.getImplementation()).thenReturn(product);
        when(((HibernateProxy) product).getHibernateLazyInitializer()).thenReturn(lazyInitializer);

        // CartItem의 product 필드에 Mock Product 설정 (리플렉션 사용)
        Field productField = CartItem.class.getDeclaredField("product");
        productField.setAccessible(true);
        productField.set(cartItems.get(0), product);

        // 재구매 확인 설정
        when(purchaseRepository.existsByUserIdAndProductId(user.getId(), product.getId())).thenReturn(true);

        // when & then
        assertThrows(ApiException.class, () -> orderService.createOrder(new AuthUser(user.getId(), user.getEmail(), user.getUserRole())));
        verify(cartService, times(1)).deleteProductItemFromCart(user, product.getId());
    }


    @Test
    void 정상_상태_변경() {
        // given
        when(orderRepository.findByOrderRequestId(orderId)).thenReturn(Optional.of(order));

        // when
        orderService.orderRequested(orderId);

        // then
        assertEquals(OrderStatus.REQUESTED, order.getStatus());
        verify(orderRepository, times(1)).findByOrderRequestId(orderId);
    }

    @Test
    void 정상_주문_삭제() {
        // given
        when(orderRepository.findByOrderRequestId(orderId)).thenReturn(Optional.of(order));

        // when
        orderService.orderFailed(orderId);

        // then
        verify(orderRepository, times(1)).delete(order);
        verify(orderRepository, times(1)).findByOrderRequestId(orderId);
    }

    @Test
    void 정상_상태_변경_및_재고_차감() throws Exception {
        // given
        when(orderRepository.findByOrderRequestId(orderId)).thenReturn(Optional.of(order));

        // ProjectWithTutor Mock 설정
        ProjectWithTutor projectWithTutor = Mockito.mock(ProjectWithTutor.class);
        when(projectWithTutor.getId()).thenReturn(1L);

        // CartItem의 Product를 ProjectWithTutor로 설정
        Field productField = CartItem.class.getDeclaredField("product");
        productField.setAccessible(true);
        productField.set(cartItems.get(0), projectWithTutor);

        // projectWithTutorService와 stockService의 Mock 설정
        doNothing().when(stockService).updateStock(1L);


        // when
        orderService.orderApproved(orderId);

        // then
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        verify(stockService, times(cartItems.size())).updateStock(1L);
        verify(orderItemService, times(1)).saveOrderItem(orderId);
    }
}
