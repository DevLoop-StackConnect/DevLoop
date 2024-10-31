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
    private final SlackFeignClient slackFeignClient;
    private final SlackUserService slackUserService;
    private final SlackMappingService slackMappingService;
    private final SlackNotificationService notificationService;

    public void handleSlackEvent(JsonNode event) {
        try {
            String eventType = event.get("event").get("type").asText();
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

    private void handleTeamJoin(JsonNode event) {
        String username = event.get("user").get("name").asText();

        NotificationMessage message = NotificationMessage.builder()
                .type(NotificationType.WORKSPACE_JOIN)
                .notificationTarget("#general")
                .data(Map.of(
                        "username", username,
                        "workspace", "DevLoop"
                ))
                .timestamp(LocalDateTime.now())
                .build();

        notificationService.sendNotification(message);
    }

    private void handleChannelJoin(JsonNode event) {
        String slackUserId = event.get("user").asText();
        try {
            String userEmail = slackUserService.findByEmail(slackUserId)
                    .getUser().getProfile().getEmail();
            slackMappingService.handleSlackJoin(userEmail);
        } catch (Exception e) {
            log.warn("채널 입장 처리 실패: {}", slackUserId, e);
        }
    }

    @Transactional
    public void verifyAndLinkAccount(Long userId, String slackId, String slackEmail) {
        if (!slackUserService.verifySlackUser(slackId)) {
            throw new ApiException(ErrorStatus._INVALID_SLACK_USER);
        }

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