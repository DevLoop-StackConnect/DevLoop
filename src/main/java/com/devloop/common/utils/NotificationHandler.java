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
    //redisTemplate 메시지 큐 저장 용도
    private final RedisTemplate<String, NotificationMessage> redisTemplate;
    //Slack API 호출을 위한 Feign Client
    private final SlackFeignClient slackFeignClient;

    //Slack 메시지 큐 이름 정의
    private static final String NOTIFICATION_QUEUE = "slack:notifications";
    //Slack 메시지 실패 큐 이름 정의
    private static final String FAILED_QUEUE = "slack:notifications:failed";
    //재시도 횟수
    private static final int MAX_RETRY_COUNT = 3;

    // notify로 시작하는 서비스 메서드 포인트 컷
    @Pointcut("execution(* com.devloop.*.service.*.notify*(..))")
    private void notificationPointcut(){}

    //notyError로 시작하는 서비스 메서드 포인트 컷
    @Pointcut("execution(* com.devloop.*.service.*.notifyError*(..))")
    private void errorNotificationPointcut() {}
    // 모든 알림 관련 메서드를 포함하는 통합 PointCut
    @Pointcut("notificationPointcut() || errorNotificationPointcut()")
    private void allNotificationPointcut(){}

    // 일반 알림 처리 메서드
    @AfterReturning("notificationPointcut()")
    public void handleNotification(JoinPoint joinPoint) {
        try {
            //알림 메시지 생성 및 전송
            NotificationMessage message = createNotificationMessage(joinPoint);
            sendNotification(message);
        } catch (Exception e) {
            log.error("알림 처리 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }
    //알림 전송 처리 메서드
    public void sendNotification(NotificationMessage message) {
        try {
            //redis 큐에 메시지 추가
            redisTemplate.opsForList().rightPush(NOTIFICATION_QUEUE, message);
            //즉시 알림이 필요한 경우 처리
            if (isInstantNotification(message.getType())) {
                sendInstantNotification(message);
            }
            log.info("알림 큐 추가됨: {}", message.getType());
        } catch (Exception e) {
            log.error("알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }

    //에러 발생 시 알림 처리 메서드
    @AfterThrowing(pointcut = "allNotificationPointcut()", throwing = "exception")
    public void handleError(JoinPoint joinPoint, Exception exception) {
        //에러 알림 메시지 생성 및 전송
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

    // 성능 모니터링
    @Around("allNotificationPointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        //시작 시간 기록
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        //처리 시간이 2초를 초과하는 경우 경고 로그 출력
        if(duration > 2000) {
            log.warn("처리 시간 초과 - 메서드: {}, 소요시간: {}ms",
                    joinPoint.getSignature().toShortString(),
                    duration);
        }

        log.info("메서드: {}, 소요시간: {}ms",
                joinPoint.getSignature().toShortString(),
                duration);
        return result;
    }

    //주기적으로 알림 큐를 처리하는 스케줄링 메서드
    @Scheduled(fixedDelay = 2000)
    public void processNotifications() {
        //redis 큐에서 알림 메시지 추출
        NotificationMessage notification = redisTemplate.opsForList()
                .leftPop(NOTIFICATION_QUEUE, 5, TimeUnit.SECONDS);

        if (notification != null) {
            try {
                //Slack으로 메시지 전송
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
    //실패한 알림을 재처리하는 스케줄링 메서드
    @Scheduled(fixedDelay = 5000)
    public void processFailedNotifications() {
        //실패 큐에서 메시지 추출
        NotificationMessage failedNotification =
                redisTemplate.opsForList().leftPop(FAILED_QUEUE, 5, TimeUnit.SECONDS);


        if (failedNotification != null) {
            try {
                //재시도 전송
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

    // AOP JoinPoint로부터 알림 메시지를 생성하는 메서드
    private NotificationMessage createNotificationMessage(JoinPoint joinPoint) {
        //메서드 시그니처 정보 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        //파라미터 이름과 값 추출
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        // 파라미터 정보를 MAP으로 변환
        Map<String, Object> data = new HashMap<>();
        for(int i = 0; i < args.length; i++) {
            data.put(parameterNames[i], args[i]);
        }
        // 메서드 이름으로부터 알림 타입 결정
        NotificationType type = NotificationType.of(methodName);
        return NotificationMessage.builder()
                .type(type)
                .notificationTarget(type.getChannelFormat())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    //즉시 알림이 필요한 타입인지 확인하는 메서드
    private boolean isInstantNotification(NotificationType type) {
        return type == NotificationType.PAYMENT ||
                type == NotificationType.COMMUNITY_COMMENT ||
                type == NotificationType.PARTY_COMMENT;
    }
    //즉시 알림이 필요한 메시지를 처리하는 메서드
    private void sendInstantNotification(NotificationMessage message) {
        try {
            //userId가 있는 경우에만 즉시 알림 처리
            String userId = (String) message.getData().get("userId");
            if (userId != null) {
                //Slack 사용자 멘션 형식으로 타겟 설정
                message.setNotificationTarget("@" + userId);
                redisTemplate.opsForList().leftPush(NOTIFICATION_QUEUE, message);
            }
        } catch (Exception e) {
            log.error("즉시 알림 전송 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }
    //실패한 알림를 처리하는 메서드
    private void handleFailedNotification(NotificationMessage notification) {
        if (notification == null) return;
        //재시도 횟수 확인
        Integer retryCount = (Integer) notification.getData()
                .getOrDefault("retryCount", 0);
        //최대 재시도 횟수 이내ㅐ의 경우 재시도 큐에 추가
        if (retryCount < MAX_RETRY_COUNT) {
            notification.getData().put("retryCount", retryCount + 1);
            redisTemplate.opsForList().rightPush(FAILED_QUEUE, notification);
            log.info("재시도 큐에 추가됨. 재시도 횟수: {}", retryCount + 1);
        } else {
            log.error("최대 재시도 횟수 초과. 알림 폐기: {}", notification);
        }
    }
    //Slack으로 메시지를 전송하는 메서드
    private void sendSlackMessage(String message, String target) {
        SlackMessage slackMessage = SlackMessage.builder()
                .text(message)
                .channel(target)
                .build();
        slackFeignClient.sendMessage(slackMessage);
    }
    //알림 타입에 따라 메시지 형식을 변환하는 메서드
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