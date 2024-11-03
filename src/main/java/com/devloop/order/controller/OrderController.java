package com.devloop.order.controller;

import com.devloop.common.AuthUser;
import com.devloop.order.entity.Order;
import com.devloop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 하기 (주문 객체 생성)
    @PostMapping("/api/v2/orders")
    public String createOrder(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        Order order = orderService.createOrder(authUser);
        return "redirect:/payments-request?orderId=" + order.getId();
    }


    // 주문 실패
    @RequestMapping("/api/v2/orders-fail")
    public String failOrder(
            @RequestParam("orderRequestId") String orderRequestId,
            @RequestParam(value = "message", required = false, defaultValue = "orderFailed") String message,
            @RequestParam(value = "code", required = false, defaultValue = "400") Integer code

    ) {
        orderService.orderFailed(orderRequestId);
        return "redirect:/payments-fail?message=" + message + "&code=" + code;
    }
}
