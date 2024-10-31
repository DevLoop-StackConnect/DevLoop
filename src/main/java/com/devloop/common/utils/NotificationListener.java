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
    private final SlackFeignClient slackFeignClient; //slack 메시지 전송을 위한 클라이언트 의존성
    private final RedisTemplate<String, NotificationMessage> redisTemplate;
    private static final String NOTIFICATION_QUEUE = "slack:notifications"; //redis에서 알림을 저장하는 Queue 키
    private static final String FAILED_QUEUE = "slack:notifications:failed"; //redis에서 실패한 알림을 저장한느 Queue 키
    private static final int MAX_RETRY_COUNT = 3; //알림 전송 실패 시 최대 재시도 횟수

    @Scheduled(fixedDelay = 1000)
    //Slack에서 알림을 처리하는 메서드
    public void processNotifications() {
        //큐에서 leftpop으로 하나 가져옴
        NotificationMessage notification = redisTemplate.opsForList()
                .leftPop(NOTIFICATION_QUEUE, 0, TimeUnit.SECONDS);

        if (notification != null) {
            try {
                // Slack 메시지 전송
                sendSlackMessage(
                        //알림 메시지 형식화 - Slack 메시지 텍스트로 변환
                        formatSlackMessage(notification),
                        //메시지 전송 대상 채널 설정
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
    //실패한 slack 알림 처리 메서드
    public void processFailedNotifications() {
        //실패 큐에서 leftpop으로 하나 가져옴
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
    //실패한 알림을 처리하는 메서드
    private void handleFailedNotification(NotificationMessage notification) {
        //null이면 바로 리턴 (작업 x)
        if (notification == null) return;
        //알림 재시도 횟수 가져오기
        Integer retryCount = (Integer) notification.getData()
                .getOrDefault("retryCount", 0);
        // 최대 재시도 횟수를 초과하지 않았을 때
        if (retryCount < MAX_RETRY_COUNT) {
            //재시도 횟수 증가
            notification.getData().put("retryCount", retryCount + 1);
            //실패 큐애 알림을 다시 추가
            redisTemplate.opsForList().rightPush(FAILED_QUEUE, notification);
            log.info("재시도 큐에 추가됨. 재시도 횟수: {}", retryCount + 1);
        } else {
            log.error("최대 재시도 횟수 초과. 알림 폐기: {}", notification);
        }
    }
    //Slack 메시지를 전송하는 메서드
    private void sendSlackMessage(String message, String target) {
        //빌드해서 메시지 텍스트,채널 설정
        SlackMessage slackMessage = SlackMessage.builder()
                .text(message)
                .channel(target)
                .build();
        //Slack 메시지 전송
        slackFeignClient.sendMessage(slackMessage);
    }
    //알림 메시지를 Slack 형식으로 포맷팅
    private String formatSlackMessage(NotificationMessage notification) {
        //알림 유형에 따라 다른 메시지 포맷 반환
        return switch (notification.getType()) {
            case WORKSPACE_JOIN -> formatWorkspaceMessage(notification);
            case INQUIRY -> formatInquiryMessage(notification);
            case PAYMENT -> formatPaymentMessage(notification);
            case COMMUNITY_COMMENT, PARTY_COMMENT -> formatCommentMessage(notification);
            case ERROR -> formatErrorMessage(notification);
            case GENERAL -> notification.getData().get("message").toString();
        };
    }
    //워크 스페이스 참여 메시지 포맷팅
    private String formatWorkspaceMessage(NotificationMessage notification) {
        //알림 data 가져오기
        Map<String, Object> data = notification.getData();
        return String.format("🎇%s님이 %s 워크스페이스에 참여하였습니다.🎇",
                data.get("username"),
                data.get("workspace")
        );
    }
    //문의 알림 포맷팅 메서드
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
    //결제 알림 포맷팅 메서드
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
    //댓글 알림 포멧팅 메서드
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
    //에러 알림 포맷팅 메서드
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