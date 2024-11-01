package com.devloop.common.utils;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.config.SlackFeignClient;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.dto.SlackMessage;
import com.devloop.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationHandler {

    private final RedisTemplate<String, NotificationMessage> redisTemplate;
    private final SlackFeignClient slackFeignClient;

    private static final String NOTIFICATION_QUEUE = "slack:notifications";
    private static final String FAILED_QUEUE = "slack:notifications:failed";
    private static final int MAX_RETRY_COUNT = 3;

    // === AOP Pointcuts ===
    @Pointcut("execution(* com.devloop.*.service.*.notify*(..))")
    private void notificationPointcut(){}

    @Pointcut("execution(* com.devloop.*.service.*.notifyError*(..))")
    private void errorNotificationPointcut() {}

    @Pointcut("notificationPointcut() || errorNotificationPointcut()")
    private void allNotificationPointcut(){}

    // === Notification Handling Methods ===
    @AfterReturning("notificationPointcut()")
    public void handleNotification(JoinPoint joinPoint) {
        try {
            NotificationMessage message = createNotificationMessage(joinPoint);
            sendNotification(message);
        } catch (Exception e) {
            log.error("알림 처리 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }

    public void sendNotification(NotificationMessage message) {
        try {
            redisTemplate.opsForList().rightPush(NOTIFICATION_QUEUE, message);

            if (isInstantNotification(message.getType())) {
                sendInstantNotification(message);
            }

            log.info("알림 큐 추가됨: {}", message.getType());
        } catch (Exception e) {
            log.error("알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }

    @AfterThrowing(pointcut = "allNotificationPointcut()", throwing = "exception")
    public void handleError(JoinPoint joinPoint, Exception exception) {
        NotificationMessage errorMessage = NotificationMessage.builder()
                .type(NotificationType.ERROR)
                .notificationTarget(NotificationType.ERROR.getChannelFormat())
                .data(Map.of(
                        "method", joinPoint.getSignature().toShortString(),
                        "error", exception.getMessage(),
                        "timestamp", LocalDateTime.now().toString()
                ))
                .timestamp(LocalDateTime.now())
                .build();
        sendNotification(errorMessage);
    }

    // === Performance Monitoring ===
    @Around("allNotificationPointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if(duration > 1000) {
            log.warn("처리 시간 초과 - 메서드: {}, 소요시간: {}ms",
                    joinPoint.getSignature().toShortString(),
                    duration);
        }

        log.info("메서드: {}, 소요시간: {}ms",
                joinPoint.getSignature().toShortString(),
                duration);

        return result;
    }

    // === Scheduled Processing Methods ===
    @Scheduled(fixedDelay = 1000)
    public void processNotifications() {
        NotificationMessage notification = redisTemplate.opsForList()
                .leftPop(NOTIFICATION_QUEUE, 0, TimeUnit.SECONDS);

        if (notification != null) {
            try {
                sendSlackMessage(
                        formatMessage(notification),
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
                        formatMessage(failedNotification),
                        failedNotification.getNotificationTarget()
                );
                log.info("재시도 알림 전송 완료: {}", failedNotification.getType());
            } catch (Exception e) {
                log.error("재시도 알림 처리 실패", e);
                handleFailedNotification(failedNotification);
            }
        }
    }

    // === Utility Methods ===
    private NotificationMessage createNotificationMessage(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> data = new HashMap<>();
        for(int i = 0; i < args.length; i++) {
            data.put(parameterNames[i], args[i]);
        }

        NotificationType type = NotificationType.of(methodName);
        return NotificationMessage.builder()
                .type(type)
                .notificationTarget(type.getChannelFormat())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
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

    private String formatMessage(NotificationMessage notification) {
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
        return String.format("🎇%s님이 %s 워크스페이스에 참여하였습니다.🎇",
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