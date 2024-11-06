package com.devloop.notification.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.notification.request.SlackLinkRequest;
import com.devloop.notification.service.SlackAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SlackConnectionController {
    //Slack 계정 매핑 관련 로직 처리하는 서비스
    private final SlackAccountService slackAccountService;

    @PostMapping("/v2/slack/connection/link")
    //Slack 연동 메서드
    public ApiResponse<Void> linkSlackAccount(
            @AuthenticationPrincipal Long userId, //인증된 사용자의 Id 주입
            @Valid @RequestBody SlackLinkRequest request //클라이언트 요청에서 Slack 연동 요청 데이터 주입
    ) {
        //Slack 계정을 검증하고 연동하는 메서드 호출
        slackAccountService.verifyAndLinkAccount(userId, request.getSlackId(), request.getSlackEmail());
        return ApiResponse.success(null);
    }

    @PostMapping("/v2/slack/connection/unlink")
    //Slack 연동 해제 메서드
    public ApiResponse<Void> unlinkSlackAccount(@AuthenticationPrincipal Long userId) {
        slackAccountService.unlinkSlackAccount(userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/v2/slack/connection/status")
    // 슬랙 계정 연동 상태 확인
    public ApiResponse<Map<String, Boolean>> checkConnectionStatus(@AuthenticationPrincipal Long userId) {
        //Slack 연동 여부 확인
        boolean isLinked = slackAccountService.isSlackLinked(userId);
        return ApiResponse.success(Map.of("isLinked", isLinked));
    }
}