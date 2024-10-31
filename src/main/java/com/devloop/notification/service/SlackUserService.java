package com.devloop.notification.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.config.SlackFeignClient;

import com.devloop.notification.response.SlackUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackUserService {

    private final SlackFeignClient slackFeignClient;
    private final ObjectMapper objectMapper;

    @Cacheable(value = "slackUsers", key = "#userId", unless = "#result == null")
    public String getSlackUserId(String userId) {
        try {
            Object userInfo = slackFeignClient.getUserInfo(userId);
            return extractSlackUserId(userInfo);
        } catch (Exception e) {
            log.error("Slack 사용자 정보 조회 실패: {}", e.getMessage());
            throw new ApiException(ErrorStatus._SLACK_USER_NOT_FOUND);
        }
    }

    public SlackUserResponse findByEmail(String email) {
        try {
            Object userInfo = slackFeignClient.getUserByEmail(email);
            return objectMapper.convertValue(userInfo, SlackUserResponse.class);
        } catch (Exception e) {
            log.error("Slack 이메일로 사용자 조회 실패: {} - {}", email, e.getMessage());
            throw new ApiException(ErrorStatus._SLACK_USER_NOT_FOUND);
        }
    }

    private String extractSlackUserId(Object userInfo) {
        try {
            SlackUserResponse response = objectMapper.convertValue(userInfo, SlackUserResponse.class);

            if (!response.isOk()) {
                log.error("Slack API 에러: {}", response.getError());
                throw new ApiException(ErrorStatus._SLACK_API_ERROR);
            }

            if (response.getUser() == null || response.getUser().getId() == null) {
                throw new ApiException(ErrorStatus._SLACK_API_ERROR);
            }

            return response.getUser().getId();
        } catch (Exception e) {
            log.error("Slack 사용자 정보 파싱 실패", e);
            throw new ApiException(ErrorStatus._SLACK_API_ERROR);
        }
    }

    public boolean verifySlackUser(String slackId) {
        try {
            Object userInfo = slackFeignClient.getUserInfo(slackId);
            SlackUserResponse response = objectMapper.convertValue(userInfo, SlackUserResponse.class);
            return response.isOk() && !response.getUser().isBot();
        } catch (Exception e) {
            log.warn("Slack 사용자 검증 실패: {}", slackId);
            return false;
        }
    }
}