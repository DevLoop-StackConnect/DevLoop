package com.devloop.aop;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.enums.NotificationType;
import com.devloop.notification.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class NotificationAspect {
    private final SlackNotificationService slackNotificationService;

    @Pointcut("execution(* com.devloop.*.service.*.notify*(..))")
    private void notificationPointcut(){}

    @Pointcut("execution(* com.devloop.*.service.*.notifyError*(..))")
    private void errorNotificationPointcut() {}

    @Pointcut("notificationPointcut() || errorNotificationPointcut()")
    private void allNotificationPointcut(){}

    @AfterReturning("notificationPointcut()")
    public void handleNotification(JoinPoint joinPoint) {
        try {
            NotificationMessage message = createNotificationMessage(joinPoint);
            slackNotificationService.sendNotification(message);
        } catch (Exception e) {
            log.error("알림 처리 실패", e);
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }

    private NotificationMessage createNotificationMessage(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String[] parameterName = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> data = new HashMap<>();
        for(int i = 0; i < args.length; i++) {
            data.put(parameterName[i], args[i]);
        }

        NotificationType type = NotificationType.of(methodName);

        return NotificationMessage.builder()
                .type(type)
                .notificationTarget(type.getChannelFormat())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
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

        slackNotificationService.sendNotification(errorMessage);
    }

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
}