package com.devloop.payment.service;

import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import com.devloop.payment.entity.Payment;
import com.devloop.payment.repository.PaymentRepository;
import com.devloop.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PurchaseService purchaseService;

    // 결제 승인 후 수행되는 로직
    @Transactional
    public void paymentCompletionLogic(JSONObject jsonObject) {
        // 구매 내역 생성
        purchaseService.createPurchase(jsonObject.get("orderId").toString());
        // 주문 상태 완료로 변경
        orderService.orderApproved(jsonObject.get("orderId").toString());
        // 결제 내역 생성
        createPayment(jsonObject);
    }

    @Transactional
    public void createPayment(JSONObject jsonObject) {

        // 주문 객체 가져오기
        Order order = orderService.findByOrderRequestId(jsonObject.get("orderId").toString());

        // totalAmount를 BigDecimal로 변환
        BigDecimal amount = new BigDecimal(jsonObject.get("totalAmount").toString());

        // requestedAt과 approvedAt을 LocalDateTime으로 변환
        LocalDateTime requestedAt = OffsetDateTime.parse(jsonObject.get("requestedAt").toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        LocalDateTime approvedAt = OffsetDateTime.parse(jsonObject.get("approvedAt").toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

        // Payment 객체 생성
        Payment payment = Payment.of(
                order.getUser(),
                order,
                amount,
                jsonObject.get("method").toString(),
                jsonObject.get("paymentKey").toString(),
                jsonObject.get("orderId").toString(),
                requestedAt,
                approvedAt
        );
        paymentRepository.save(payment);
    }
}
