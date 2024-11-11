package com.devloop.payment.service;

import com.devloop.cart.entity.Cart;
import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import com.devloop.payment.entity.Payment;
import com.devloop.payment.repository.PaymentRepository;
import com.devloop.payment.service.PaymentService;
import com.devloop.purchase.service.PurchaseService;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PaymentService paymentService;

    private JSONObject jsonObject;
    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("홍길동", "test@test.com", "123asbK!", UserRole.ROLE_USER);
        // 주문 객체 초기화
        order = Order.of(
                BigDecimal.TEN,
                "order-123",
                "Test Order",
                user,
                Cart.of(BigDecimal.TEN, user)
        );

        // 결제 데이터 초기화
        jsonObject = new JSONObject();
        jsonObject.put("orderId", "order-123");
        jsonObject.put("totalAmount", "100.00");
        jsonObject.put("method", "CREDIT_CARD");
        jsonObject.put("paymentKey", "paykey-123");
        jsonObject.put("requestedAt", OffsetDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        jsonObject.put("approvedAt", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Test
    void 결제_완료_로직_성공() {
        // given
        when(orderService.findByOrderRequestId("order-123")).thenReturn(order);

        // when
        paymentService.paymentCompletionLogic(jsonObject);

        // then
        verify(purchaseService, times(1)).createPurchase("order-123");
        verify(orderService, times(1)).orderApproved("order-123");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void 결제_생성_성공() {
        // given
        when(orderService.findByOrderRequestId("order-123")).thenReturn(order);

        // 결제 데이터 파싱
        BigDecimal amount = new BigDecimal("100.00");
        LocalDateTime requestedAt = OffsetDateTime.parse(jsonObject.get("requestedAt").toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        LocalDateTime approvedAt = OffsetDateTime.parse(jsonObject.get("approvedAt").toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

        // when
        paymentService.createPayment(jsonObject);

        // then
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderService, times(1)).findByOrderRequestId("order-123");
    }
}
