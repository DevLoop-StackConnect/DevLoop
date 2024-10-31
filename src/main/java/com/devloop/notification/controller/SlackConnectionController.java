package com.devloop.notification.controller;

import com.devloop.common.apipayload.ApiResponse;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.notification.request.SlackLinkRequest;
import com.devloop.notification.service.SlackAppService;
import com.devloop.notification.service.SlackMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/slack/connection")
@Slf4j
public class SlackConnectionController {
    private final SlackMappingService slackMappingService;
    private final SlackAppService slackAppService;

    @PostMapping("/link")
    public ApiResponse linkSlackAccount(
            @AuthenticationPrincipal Long userId,
            @RequestBody SlackLinkRequest request
    ) {
        try {
            slackAppService.verifyAndLinkAccount(
                    userId,
                    request.getSlackId(),
                    request.getSlackEmail()
            );
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Slack 계정 연동 실패", e);
            throw new ApiException(ErrorStatus._SLACK_LINK_ERROR);
        }
    }

    @PostMapping("/unlink")
    public ApiResponse unlinkSlackAccount(@AuthenticationPrincipal Long userId) {
        try {
            slackMappingService.unlinkSlackAccount(userId);
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Slack 계정 연동 해제 실패", e);
            throw new ApiException(ErrorStatus._SLACK_UNLINK_ERROR);
        }
    }

    @GetMapping("/status")
    public ApiResponse checkConnectionStatus(@AuthenticationPrincipal Long userId) {
        try {
            boolean isLinked = slackMappingService.isSlackLinked(userId);
            return ApiResponse.success(Map.of("isLinked", isLinked));
        } catch (Exception e) {
            log.error("Slack 연동 상태 확인 실패", e);
            throw new ApiException(ErrorStatus._SLACK_STATUS_CHECK_ERROR);
        }
    }
}