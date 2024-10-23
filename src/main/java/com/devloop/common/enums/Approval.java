package com.devloop.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Approval {
    WAITE("승인 대기"),  // 승인 대기 상태
    APPROVED("승인 완료");    // 승인 된 상태

    private final String status;
}
