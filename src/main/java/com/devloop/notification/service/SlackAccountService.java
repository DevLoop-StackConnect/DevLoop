package com.devloop.notification.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.NotificationHandler;
import com.devloop.config.SlackProperties;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.entity.SlackUserMapping;
import com.devloop.notification.enums.NotificationType;
import com.devloop.notification.repository.SlackUserMappingRepository;
import com.devloop.notification.response.SlackUserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import com.devloop.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackAccountService {

    private final UserService userService;
    private final SlackUserMappingRepository mappingRepository;
    private final SlackUserService slackUserService;
    private final NotificationHandler notificationHandler;
    private final SlackProperties slackProperties;
    private final ObjectMapper objectMapper;

    //Slack  이벤트 처리하는 메서드
    public void handleSlackEvent(JsonNode event) {
        try {
            //이벤트, 타입에 따른 분기 처리
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

    /// 팀 가입 이벤트 처리
    private void handleTeamJoin(JsonNode event) {
        //가입한 사용자 이름 추출
        String username = event.get("user").get("name").asText();
        //워크스페이스 가입 알림 메시지ㅣ 생성
        NotificationMessage message = NotificationMessage.builder()
                .type(NotificationType.WORKSPACE_JOIN)
                .notificationTarget("#general")
                .data(Map.of(
                        "username", username,
                        "workspace", "DevLoop"
                ))
                .timestamp(LocalDateTime.now())
                .build();

        notificationHandler.sendNotification(message);
    }

    //채널 가입 이벤트 처리
    private void handleChannelJoin(JsonNode event) {
        String slackUserId = event.get("user").asText();
        String channelId = event.get("channel").asText();  // 채널 ID 추가

        try {
            // Slack 사용자 정보 조회 및 이메일 추출
            SlackUserResponse slackUser = slackUserService.findByEmail(slackUserId);
            String userEmail = slackUser.getUser().getProfile().getEmail();
            String username = slackUser.getUser().getName();  // 사용자 이름 추가

            // 1. 매핑 처리
            handleSlackJoin(userEmail);

            // 2. 채널 참여 알림 전송 추가
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.WORKSPACE_JOIN)  // 또는 새로운 타입 CHANNEL_JOIN 추가 가능
                    .notificationTarget(channelId)  // 해당 채널에 알림
                    .data(Map.of(
                            "username", username,
                            "channelId", channelId,
                            "action", "channel_join",
                            "timestamp", LocalDateTime.now().toString()
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationHandler.sendNotification(message);
            log.info("채널 입장 알림 전송 완료 - user: {}, channel: {}", username, channelId);

        } catch (Exception e) {
            log.warn("채널 입장 처리 실패: {}", slackUserId, e);
        }
    }

    //Slack 계정 검증 및 연동 처리
    @Transactional
    public void verifyAndLinkAccount(Long userId, String slackId, String slackEmail) {
        try {
            // Slack 사용자 검증
            if (!slackUserService.verifySlackUser(slackId)) {
                throw new ApiException(ErrorStatus._INVALID_SLACK_USER);
            }
            // 매핑 생성
            createMapping(userId, slackId, slackEmail);
            // 연동 완료 알림
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.GENERAL)
                    .notificationTarget("@" + slackId)
                    .data(Map.of("message", "DevLoop 계정과 Slack 계정이 성공적으로 연동되었습니다."))
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationHandler.sendNotification(message);
            log.info("Slack 계정 연동 완료. userId: {}, slackId: {}", userId, slackId);
        } catch (Exception e) {
            log.error("Slack 계정 연동 실패", e);
            throw new ApiException(ErrorStatus._SLACK_LINK_ERROR);
        }
    }

    //Slack 매핑 정보 생성 메서드
    @Transactional
    public void createMapping(Long userId, String slackId, String slackEmail) {
        User user = userService.findById(userId);

        // 기존 매핑 비활성화
        mappingRepository.deactivateAllByUserId(user.getId());

        // 새 매핑 생성
        SlackUserMapping mapping = SlackUserMapping.of(
                user, slackId, slackEmail
        );

        mappingRepository.save(mapping);
        user.updateSlackInfo(slackId, slackEmail);
    }

    //Slack 채널 가입 메서드
    @Transactional
    public void handleSlackJoin(String slackEmail) {
        try {
            SlackUserResponse slackUser = slackUserService.findByEmail(slackEmail);
            userService.getUserEmail(slackEmail).ifPresent(user ->
                    createMapping(
                            user.getId(),
                            slackUser.getUser().getId(),
                            slackEmail
                    )
            );
        } catch (Exception e) {
            log.warn("Slack 자동 매핑 실패: {}", slackEmail, e);
        }
    }

    //Slack 계정 연동해제 메서드
    @Transactional
    public void unlinkSlackAccount(Long userId) {
        try {
            User user = userService.findById(userId);

            // 기존 매핑 모두 비활성화
            mappingRepository.deactivateAllByUserId(user.getId());

            // 사용자의 Slack 정보 초기화
            user.unlinkSlack();
            userService.save(user);

            log.info("Slack 계정 연동 해제 완료. userId: {}", userId);
        } catch (Exception e) {
            log.error("Slack 계정 연동 해제 실패", e);
            throw new ApiException(ErrorStatus._SLACK_UNLINK_ERROR);
        }
    }

    //Slack 계정 연동 상태 확인 메서드
    public boolean isSlackLinked(Long userId) {
        try {
            User user = userService.findById(userId);

            return user.isSlackLinked() &&
                    mappingRepository.findByUserIdAndActiveTrue(userId).isPresent();
        } catch (Exception e) {
            log.error("Slack 연동 상태 확인 실패. userId: {}", userId);
            return false;
        }
    }

    public ResponseEntity<?> processSlackEvent(String requestBody, String signature, String timestamp) {
        try {
            if (!isValidSlackRequest(requestBody, signature, timestamp)) {
                log.warn("유효하지 않은 Slack 요청이 감지되었습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            JsonNode event = objectMapper.readTree(requestBody);

            if (event.has("challenge")) {
                return ResponseEntity.ok(event.get("challenge").asText());
            }

            handleSlackEvent(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Slack 이벤트 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidSlackRequest(String requestBody, String signature, String timestamp) {
        try {
            long timestampLong = Long.parseLong(timestamp);
            long now = System.currentTimeMillis() / 1000;
            if (Math.abs(now - timestampLong) > 300) {
                log.warn("타임스탬프 만료");
                return false;
            }

            String baseString = String.format("v0:%s:%s", timestamp, requestBody);
            String mySignature = "v0=" + HmacUtils.hmacSha256Hex(
                    slackProperties.getApp().getSigningSecret(),
                    baseString
            );
            return mySignature.equals(signature);
        } catch (Exception e) {
            log.error("Signature 검증 실패", e);
            return false;
        }
    }
}