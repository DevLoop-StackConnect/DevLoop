package com.devloop.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    WAIT("주문 대기"),
    REQUESTED("주문 요청됨"),
    APPROVED("주문 승인됨");

    private final String orderStatus;

}
