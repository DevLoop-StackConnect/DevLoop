package com.devloop.purchase.service;

import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import com.devloop.purchase.entity.Purchase;
import com.devloop.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final OrderService orderService;

    // 구매 내역 생성
    @Transactional
    public void createPurchase(String orderRequestId) {

        // 주문 객체 가져오기
        Order order = orderService.findByOrderRequestId(orderRequestId);

        // Purchase 객체 생성
        List<Purchase> purchases = order.getCart().getItems().stream()
                .map(p->Purchase.from(p.getProduct(), order.getUser()))
                .collect(Collectors.toList());

        // Purchase 객체 저장
        purchaseRepository.saveAll(purchases);
    }
}
