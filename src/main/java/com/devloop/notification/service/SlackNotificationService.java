package com.devloop.notification.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.config.SlackFeignClient;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.dto.SlackMessage;
import com.devloop.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackNotificationService {
    private final RedisTemplate<String, NotificationMessage> redisTemplate;
    private static final String NOTIFICATION_QUEUE = "slack:notifications";

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void sendNotification(NotificationMessage message) {
        try {
            redisTemplate.opsForList().rightPush(NOTIFICATION_QUEUE, message);

            if (isInstantNotification(message.getType())) {
                sendInstantNotification(message);
            }
            log.info("알림 큐 추가됨: {}", message.getType());
        } catch (Exception e) {
            log.error("알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_QUEUE_ERROR);
        }
    }

    private boolean isInstantNotification(NotificationType type) {
        return type == NotificationType.PAYMENT ||
                type == NotificationType.COMMUNITY_COMMENT ||
                type == NotificationType.PARTY_COMMENT;
    }

    private void sendInstantNotification(NotificationMessage message) {
        try {
            String userId = (String) message.getData().get("userId");
            if (userId != null) {
                message.setNotificationTarget("@" + userId);
                redisTemplate.opsForList().leftPush(NOTIFICATION_QUEUE, message);
            }
        } catch (Exception e) {
            log.error("즉시 알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }
}