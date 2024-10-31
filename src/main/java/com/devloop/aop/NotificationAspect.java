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

    //슬랙 알림 서비스 주입
    private final SlackNotificationService slackNotificationService;

    //service 패키지에 notify로 시작하는 메서드 포인트컷
    @Pointcut("execution(* com.devloop.*.service.*.notify*(..))")
    private void notificationPointcut(){}
    //service 패키지에 notifyError로 시작하는 메서드 포인트컷
    @Pointcut("execution(* com.devloop.*.service.*.notifyError*(..))")
    private void errorNotificationPointcut() {}
    //위에 두개를 결합
    @Pointcut("notificationPointcut() || errorNotificationPointcut()")
    private void allNotificationPointcut(){}

    //notificationPointcut 경로의 메서드가 반환된 후 실행
    @AfterReturning("notificationPointcut()")
    //joinpoint = notificationPointcut의 데이터를 가지고 있음
    public void handleNotification(JoinPoint joinPoint) {
        try {
            //알림 메시지 생성
            NotificationMessage message = createNotificationMessage(joinPoint);
            //생성된 메시지를 Slack 알림으로 전송
            slackNotificationService.sendNotification(message);
        } catch (Exception e) {
            log.error("알림 처리 실패", e);
            //알림 전송 오류로 ApiException 작동
            throw new ApiException(ErrorStatus._NOTIFICATION_SEND_ERROR);
        }
    }

    //알리 ㅁ메시지를 생성하는 메서드
    private NotificationMessage createNotificationMessage(JoinPoint joinPoint) {
        //메서드 시그니처 가져오기 ****** 메서드 시그니처란 : 메서드가 어떤 파라미터를 받는지 포함하는 정보 ******
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //메서드 이름 가져오기
        String methodName = signature.getName();
        //메서드 파라미터 이름 배열 가져오기
        String[] parameterName = signature.getParameterNames();
        //메서드의 실제 전달된 인수 배열 가져오기
        Object[] args = joinPoint.getArgs();

        //파라미터 이름과 값을 저장할 맵
        Map<String, Object> data = new HashMap<>();
        for(int i = 0; i < args.length; i++) {
            data.put(parameterName[i], args[i]);
        }
        //메서드 이름을 기반으로 알림 유형 결정
        NotificationType type = NotificationType.of(methodName);
        //Notification 빌드하여 객체 생성
        return NotificationMessage.builder()
                .type(type)
                .notificationTarget(type.getChannelFormat())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    //알림 관련 메서드에서 예외 발생 시 실행
    @AfterThrowing(pointcut = "allNotificationPointcut()", throwing = "exception")
    public void handleError(JoinPoint joinPoint, Exception exception) {
        //오류 발생 시 알림을 처리하는 메서드
        NotificationMessage errorMessage = NotificationMessage.builder()
                .type(NotificationType.ERROR)
                .notificationTarget(NotificationType.ERROR.getChannelFormat())
                //오류 정보를 담은 데이터 설정
                .data(Map.of(
                        //예외 발생한 메서드 이름 추가
                        "method", joinPoint.getSignature().toShortString(),
                        //예외 메시지 추가
                        "error", exception.getMessage(),
                        "timestamp", LocalDateTime.now().toString()
                ))
                .timestamp(LocalDateTime.now())
                .build();
        //생성된 오류 알림 메시지를 Slack에 전송
        slackNotificationService.sendNotification(errorMessage);
    }
    //알림 관련 포인트컷에서 메서드 실행 전/후로 실행
    @Around("allNotificationPointcut()")
    //성능 모니터링을 수행하는 메서드
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if(duration > 1000) {  //실행 시간이 1초 이상인 경우
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