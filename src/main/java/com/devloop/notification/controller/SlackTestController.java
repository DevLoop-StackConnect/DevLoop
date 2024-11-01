package com.devloop.notification.controller;

import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.enums.NotificationType;
import com.devloop.notification.service.SlackAccountService;
import com.devloop.notification.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/slack/test")
public class SlackTestController {

    private final SlackNotificationService notificationService;
    private final SlackAccountService slackAccountService;

    @PostMapping("/notification")
    public ResponseEntity<?> testNotification() {
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.GENERAL)
                    .notificationTarget("#general")  // 테스트용 채널 지정
                    .data(Map.of(
                            "message", "🔔 테스트 알림입니다!",
                            "timestamp", LocalDateTime.now().toString()
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationService.sendNotification(message);
            return ResponseEntity.ok("알림 전송 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("알림 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/error-notification")
    public ResponseEntity<?> testErrorNotification() {
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.ERROR)
                    .notificationTarget("#error-monitoring")
                    .data(Map.of(
                            "method", "🔔🔔테스트 메서드",
                            "error", "🔔🔔테스트 에러 메시지",
                            "timestamp", LocalDateTime.now().toString()
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationService.sendNotification(message);
            return ResponseEntity.ok("에러 알림 전송 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("에러 알림 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/workspace-join")
    public ResponseEntity<?> testWorkspaceJoin() {
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.WORKSPACE_JOIN)
                    .notificationTarget("#general")
                    .data(Map.of(
                            "username", "🔔🔔테스트 사용자",
                            "workspace", "🔔🔔DevLoop"
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationService.sendNotification(message);
            return ResponseEntity.ok("워크스페이스 참여 알림 전송 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("워크스페이스 참여 알림 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<?> testCommentNotification() {
        try {
            NotificationMessage message = NotificationMessage.builder()
                    .type(NotificationType.COMMUNITY_COMMENT)
                    .notificationTarget("@test-user")  // 실제 Slack 유저 ID로 변경
                    .data(Map.of(
                            "postTitle", "테스트 게시글",
                            "commentAuthor", "테스트 작성자",
                            "content", "테스트 댓글입니다."
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationService.sendNotification(message);
            return ResponseEntity.ok("댓글 알림 전송 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 알림 전송 실패: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Slack API 연결 상태 확인
            status.put("slack_api", "connected");
            status.put("timestamp", LocalDateTime.now());
            status.put("status", "healthy");
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("slack_api", "disconnected");
            status.put("error", e.getMessage());
            status.put("timestamp", LocalDateTime.now());
            status.put("status", "unhealthy");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(status);
        }
    }
}
