package com.devloop.common.utils;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.config.SlackFeignClient;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.dto.SlackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {
    private final SlackFeignClient slackFeignClient;
    private final RedisTemplate<String, NotificationMessage> redisTemplate;
    private static final String NOTIFICATION_QUEUE = "slack:notifications";
    private static final String FAILED_QUEUE = "slack:notifications:failed";
    private static final int MAX_RETRY_COUNT = 3;

    @Scheduled(fixedDelay = 1000)
    public void processNotifications() {
        NotificationMessage notification = redisTemplate.opsForList()
                .leftPop(NOTIFICATION_QUEUE, 0, TimeUnit.SECONDS);

        if (notification != null) {
            try {
                sendSlackMessage(
                        formatSlackMessage(notification),
                        notification.getNotificationTarget()
                );
                log.info("알림 전송 완료: {}", notification.getType());
            } catch (Exception e) {
                log.error("알림 처리 실패", e);
                handleFailedNotification(notification);
            }
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void processFailedNotifications() {
        NotificationMessage failedNotification =
                redisTemplate.opsForList().leftPop(FAILED_QUEUE, 0, TimeUnit.SECONDS);

        if (failedNotification != null) {
            try {
                sendSlackMessage(
                        formatSlackMessage(failedNotification),
                        failedNotification.getNotificationTarget()
                );
                log.info("재시도 알림 전송 완료: {}", failedNotification.getType());
            } catch (Exception e) {
                log.error("재시도 알림 처리 실패", e);
                handleFailedNotification(failedNotification);
            }
        }
    }

    private void handleFailedNotification(NotificationMessage notification) {
        if (notification == null) return;

        Integer retryCount = (Integer) notification.getData()
                .getOrDefault("retryCount", 0);

        if (retryCount < MAX_RETRY_COUNT) {
            notification.getData().put("retryCount", retryCount + 1);
            redisTemplate.opsForList().rightPush(FAILED_QUEUE, notification);
            log.info("재시도 큐에 추가됨. 재시도 횟수: {}", retryCount + 1);
        } else {
            log.error("최대 재시도 횟수 초과. 알림 폐기: {}", notification);
        }
    }

    private void sendSlackMessage(String message, String target) {
        SlackMessage slackMessage = SlackMessage.builder()
                .text(message)
                .channel(target)
                .build();

        slackFeignClient.sendMessage(slackMessage);
    }

    private String formatSlackMessage(NotificationMessage notification) {
        return switch (notification.getType()) {
            case WORKSPACE_JOIN -> formatWorkspaceMessage(notification);
            case INQUIRY -> formatInquiryMessage(notification);
            case PAYMENT -> formatPaymentMessage(notification);
            case COMMUNITY_COMMENT, PARTY_COMMENT -> formatCommentMessage(notification);
            case ERROR -> formatErrorMessage(notification);
            case GENERAL -> notification.getData().get("message").toString();
        };
    }

    private String formatWorkspaceMessage(NotificationMessage notification) {
        Map<String, Object> data = notification.getData();
        return String.format("%s님이 %s 워크스페이스에 참여하였습니다.",
                data.get("username"),
                data.get("workspace")
        );
    }

    private String formatInquiryMessage(NotificationMessage notification) {
        Map<String, Object> data = notification.getData();
        return String.format("""
            📬 *새로운 문의*
            *작성자:* %s
            *제목:* %s
            *내용:* %s
            """,
                data.get("author"),
                data.get("title"),
                data.get("content")
        );
    }

    private String formatPaymentMessage(NotificationMessage notification) {
        Map<String, Object> data = notification.getData();
        return (boolean)data.get("success") ?
                String.format("""
                ✅ *결제 완료*
                *결제자:* %s
                *금액:* %s원
                """,
                        data.get("username"),
                        data.get("amount")
                ) :
                String.format("""
                ❌ *결제 실패*
                *결제자:* %s
                *실패 사유:* %s
                """,
                        data.get("username"),
                        data.get("errorMessage")
                );
    }

    private String formatCommentMessage(NotificationMessage notification) {
        Map<String, Object> data = notification.getData();
        return String.format("""
            💬 *새로운 댓글*
            *게시글:* %s
            *작성자:* %s
            *내용:* %s
            """,
                data.get("postTitle"),
                data.get("commentAuthor"),
                data.get("content")
        );
    }

    private String formatErrorMessage(NotificationMessage notification) {
        Map<String, Object> data = notification.getData();
        return String.format("""
            🔴 *오류 발생*
            *메서드:* %s
            *오류:* %s
            *시간:* %s
            """,
                data.get("method"),
                data.get("error"),
                data.get("timestamp")
        );
    }
}