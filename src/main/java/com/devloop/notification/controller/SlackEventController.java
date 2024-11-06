package com.devloop.notification.controller;

import com.devloop.config.SlackProperties;
import com.devloop.notification.service.SlackAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")  // 이대로 유지
public class SlackEventController {
    private final SlackAccountService slackAccountService;

    @PostMapping("/v1/slack/events")
    // ? : wildcard - 어느 타입이든 반환할 수 있다는 의미
    public ResponseEntity<?> handleSlackEvent(
            @RequestBody String requestBody, //Slack의 서명 방식 때문에 HTTP 본문 요청 requestBody 값을 가져옴
            @RequestHeader("X-Slack-Signature") String signature, //요청 헤더에 Slack 서명 값을 가져옴
            @RequestHeader("X-Slack-Request-Timestamp") String timestamp // 요청 해더이서 Slack 요청 타임스탬프 가져옴
    ) {
        return slackAccountService.processSlackEvent(requestBody, signature, timestamp);
    }
}