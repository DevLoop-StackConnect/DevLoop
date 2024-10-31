package com.devloop.notification.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.config.SlackFeignClient;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.enums.NotificationType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackAppService {
    private final SlackUserService slackUserService;
    private final SlackMappingService slackMappingService;
    private final SlackNotificationService notificationService;
    // Slack 이벤트를 처리하는 메서드
    public void handleSlackEvent(JsonNode event) {
        try {
            //이벤트 타입 추출
            String eventType = event.get("event").get("type").asText();
            //최초 수행시에만 (ex 처음 워크스페이스 가입, 처음 채널에 입장 등등
            switch (eventType) {
                case "team_join" -> handleTeamJoin(event.get("event"));
                case "member_joined_channel" -> handleChannelJoin(event.get("event"));
                default -> log.info("미처리 이벤트 타입: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Slack 이벤트 처리 실패", e);
            throw new ApiException(ErrorStatus._SLACK_EVENT_HANDLING_ERROR);
        }
    }
    //새로 가입한 사용자 처리 메서드
    private void handleTeamJoin(JsonNode event) {
        //사용자 이름 추출
        String username = event.get("user").get("name").asText();
        //알림 메시지 빌드
        NotificationMessage message = NotificationMessage.builder()
                .type(NotificationType.WORKSPACE_JOIN)
                .notificationTarget("#general")
                .data(Map.of(
                        "username", username,
                        "workspace", "DevLoop"
                ))
                .timestamp(LocalDateTime.now())
                .build();
        //알림 전송
        notificationService.sendNotification(message);
    }
    // 특정 채널에 새로 가입한 사용자 처리 메서드
    private void handleChannelJoin(JsonNode event) {
        //Slack 사용자 Id 추출
        String slackUserId = event.get("user").asText();
        try {
            String userEmail = slackUserService.findByEmail(slackUserId)
                    .getUser().getProfile().getEmail();
            //이메일 통해 특정 채널 가입 처리
            slackMappingService.handleSlackJoin(userEmail);
        } catch (Exception e) {
            log.warn("채널 입장 처리 실패: {}", slackUserId, e);
        }
    }

    @Transactional
    //Slack 계정과 애플리케이션 계정을 연동하는 메서드
    public void verifyAndLinkAccount(Long userId, String slackId, String slackEmail) {
        //Slack ID 검증
        if (!slackUserService.verifySlackUser(slackId)) {
            throw new ApiException(ErrorStatus._INVALID_SLACK_USER);
        }
        //사용자와 Slack 사용자 매핑
        slackMappingService.createMapping(userId, slackId, slackEmail);

        NotificationMessage message = NotificationMessage.builder()
                .type(NotificationType.GENERAL)
                .notificationTarget("@" + slackId)
                .data(Map.of("message", "DevLoop 계정과 Slack 계정이 성공적으로 연동되었습니다."))
                .timestamp(LocalDateTime.now())
                .build();

        notificationService.sendNotification(message);
    }
}