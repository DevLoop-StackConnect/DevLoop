package com.devloop.notification.controller;

import com.devloop.config.SlackProperties;
import com.devloop.notification.service.SlackAccountService;
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
    private final SlackAccountService slackAccountService;
    private final SlackProperties slackProperties;
    private final ObjectMapper objectMapper;

    @PostMapping
    // ? : wildcard - 어느 타입이든 반환할 수 있다는 의미
    public ResponseEntity<?> handleSlackEvent(
            @RequestBody String requestBody, //Slack의 서명 방식 때문에 HTTP 본문 요청 requestBody 값을 가져옴
            @RequestHeader("X-Slack-Signature") String signature, //요청 헤더에 Slack 서명 값을 가져옴
            @RequestHeader("X-Slack-Request-Timestamp") String timestamp // 요청 해더이서 Slack 요청 타임스탬프 가져옴
    ) throws Exception {
        // 요청 검증
        if (!isValidSlackRequest(requestBody, signature, timestamp)) {
            log.warn("유효하지 않은 Slack 요청이 감지되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //요청 본문 Json으로 파싱
        JsonNode event = objectMapper.readTree(requestBody);

        // Challenge 요청 처리 - Slack에서 서버의 URL이 유효한지 확인하는 보안 검증
        if (event.has("challenge")) {
            //Json으로 challenge 키값을 문자열로 가져옴
            return ResponseEntity.ok(event.get("challenge").asText());
        }

        // 이벤트 처리
        try {
            //slack 이벤트를 서비스 처리
            slackAccountService.handleSlackEvent(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Slack 이벤트 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //slack 요청 검증 메서드
    private boolean isValidSlackRequest(String requestBody, String signature, String timestamp) {
        try {
            // 타임스탬프 검증 (5분 이내)
            long timestampLong = Long.parseLong(timestamp);
            long now = System.currentTimeMillis() / 1000;
            if (Math.abs(now - timestampLong) > 300) {
                log.warn("타임스탬프 만료");
                return false;
            }
            //서명 검증을 위한 기본 문자열 생성
            String baseString = String.format("v0:%s:%s", timestamp, requestBody);
            //서명 생성 후 Slack 설정에서 서명 비밀 키 가져오기
            String mySignature = "v0=" + HmacUtils.hmacSha256Hex(
                    slackProperties.getApp().getSigningSecret(),
                    baseString
            );
            //요청 서명과 생성된 서명을 비교하여 검증
            return mySignature.equals(signature);
        } catch (Exception e) {
            log.error("Signature 검증 실패", e);
            return false;
        }
    }
}