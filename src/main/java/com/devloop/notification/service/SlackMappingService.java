package com.devloop.notification.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.notification.entity.SlackUserMapping;
import com.devloop.notification.repository.SlackUserMappingRepository;
import com.devloop.notification.response.SlackUserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SlackMappingService {
    private final UserRepository userRepository;
    private final SlackUserMappingRepository mappingRepository;
    private final SlackUserService slackUserService;

    @Transactional
    public void createMapping(Long userId, String slackId, String slackEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // 기존 매핑 비활성화
        mappingRepository.deactivateAllByUserId(user.getId());

        // 새 매핑 생성
        SlackUserMapping mapping = SlackUserMapping.builder()
                .user(user)
                .slackId(slackId)
                .slackEmail(slackEmail)
                .build();

        mappingRepository.save(mapping);
        user.updateSlackInfo(slackId, slackEmail);
    }

    @Transactional
    //Slack 사용자의 채널 가입 처리
    public void handleSlackJoin(String slackEmail) {
        try {
            SlackUserResponse slackUser = slackUserService.findByEmail(slackEmail);

            // 이메일로 사용자 찾기
            userRepository.findByEmail(slackEmail).ifPresent(user -> {
                createMapping(
                        user.getId(),
                        slackUser.getUser().getId(),
                        slackEmail
                );
            });
        } catch (Exception e) {
            log.warn("Slack 자동 매핑 실패: {}", slackEmail, e);
        }
    }

    @Transactional
    public void unlinkSlackAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // 기존 매핑 모두 비활성화
        mappingRepository.deactivateAllByUserId(user.getId());

        // 사용자의 Slack 정보 초기화
        user.unlinkSlack();
        userRepository.save(user);

        log.info("Slack 계정 연동 해제 완료. userId: {}", userId);
    }

    // 연동 상태 확인 메서드 추가
    public boolean isSlackLinked(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

            return user.isSlackLinked() &&
                    mappingRepository.findByUserIdAndActiveTrue(userId).isPresent();
        } catch (Exception e) {
            log.error("Slack 연동 상태 확인 실패. userId: {}", userId);
            return false;
        }
    }
}
