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
    private static final String NOTIFICATION_QUEUE = "slack:notifications"; //redis에서 사용하는 알림 큐

    //실패 시 재시도 설정 어노테이션
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
            //재시도 시 1초 지연 설정
    )

    //알림을 큐에 추가하는 메서드
    public void sendNotification(NotificationMessage message) {
        try {
            //알림 메시지를 큐에 추가
            redisTemplate.opsForList().rightPush(NOTIFICATION_QUEUE, message);
            //즉시 알림 대상인지 확인
            if (isInstantNotification(message.getType())) {
                sendInstantNotification(message);
            }
            log.info("알림 큐 추가됨: {}", message.getType());
        } catch (Exception e) {
            log.error("알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_QUEUE_ERROR);
        }
    }
    //즉시 알림 대상인지 확인하는 메서드
    private boolean isInstantNotification(NotificationType type) {
        return type == NotificationType.PAYMENT ||
                type == NotificationType.COMMUNITY_COMMENT ||
                type == NotificationType.PARTY_COMMENT;
    }
    //즉시 알림 전송 메서드
    private void sendInstantNotification(NotificationMessage message) {
        try {
            //알림 데이터에서 사용자 ID 가져오기
            String userId = (String) message.getData().get("userId");
            if (userId != null) {
                //사용자 ID를 기반 알림 대상 설정
                message.setNotificationTarget("@" + userId);
                //즉시 알림 메시지를 큐에 추가
                redisTemplate.opsForList().leftPush(NOTIFICATION_QUEUE, message);
            }
        } catch (Exception e) {
            log.error("즉시 알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }
}