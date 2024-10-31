package com.devloop.notification.controller;

import com.devloop.config.SlackProperties;
import com.devloop.notification.service.SlackAppService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/slack/events")  // 이대로 유지
@Slf4j
public class SlackEventController {
    private final SlackAppService slackAppService;
    private final SlackProperties slackProperties;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> handleSlackEvent(
            @RequestBody String requestBody,
            @RequestHeader("X-Slack-Signature") String signature,
            @RequestHeader("X-Slack-Request-Timestamp") String timestamp
    ) throws Exception {
        // 요청 검증
        if (!isValidSlackRequest(requestBody, signature, timestamp)) {
            log.warn("Invalid Slack request detected");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        JsonNode event = objectMapper.readTree(requestBody);

        // Challenge 요청 처리
        if (event.has("challenge")) {
            return ResponseEntity.ok(event.get("challenge").asText());
        }

        // 이벤트 처리
        try {
            slackAppService.handleSlackEvent(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Slack 이벤트 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidSlackRequest(String requestBody, String signature, String timestamp) {
        try {
            // 타임스탬프 검증 (5분 이내)
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

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Slack Event Endpoint is healthy");
    }
}