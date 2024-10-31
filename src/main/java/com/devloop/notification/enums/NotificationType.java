package com.devloop.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum NotificationType {
    WORKSPACE_JOIN("#데브루프"),
    PAYMENT("@{userId}"),
    INQUIRY("#문의-채널"),
    ERROR("#에러-모니터링"),
    COMMUNITY_COMMENT("@{userId}"),
    PARTY_COMMENT("@{userId}"),
    GENERAL("#데브루프");

    private final String channelFormat;

    public static NotificationType of(String methodName) {
        if (methodName.contains("WorkspaceJoin")) return WORKSPACE_JOIN;
        if (methodName.contains("Payment")) return PAYMENT;
        if (methodName.contains("Inquiry")) return INQUIRY;
        if (methodName.contains("CommunityComment")) return COMMUNITY_COMMENT;
        if (methodName.contains("PartyComment")) return PARTY_COMMENT;
        if (methodName.contains("Error")) return ERROR;
        return GENERAL;
    }
}