package com.devloop.payment.service;

import com.devloop.order.entity.Order;
import com.devloop.order.repository.OrderRepository;
import com.devloop.order.service.OrderService;
import com.devloop.payment.entity.Payment;
import com.devloop.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public void createPayment(JSONObject jsonObject){

        // 주문 객체 가져오기
        Order order = orderService.findByOrderRequestId(jsonObject.get("orderId").toString());

        // totalAmount를 BigDecimal로 변환
        BigDecimal amount = new BigDecimal(jsonObject.get("totalAmount").toString());

        // requestedAt과 approvedAt을 LocalDateTime으로 변환
        LocalDateTime requestedAt = LocalDateTime.parse(jsonObject.get("requestedAt").toString());
        LocalDateTime approvedAt = LocalDateTime.parse(jsonObject.get("approvedAt").toString());

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
