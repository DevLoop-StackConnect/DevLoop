package com.devloop.notification.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.NotificationHandler;
import com.devloop.config.SlackProperties;
import com.devloop.notification.dto.NotificationMessage;
import com.devloop.notification.entity.SlackUserMapping;
import com.devloop.notification.repository.SlackUserMappingRepository;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SlackAccountServiceTest {
    private SlackAccountService slackAccountService;
    private UserService userService;
    private SlackUserMappingRepository mappingRepository;
    private SlackUserService slackUserService;
    private NotificationHandler notificationHandler;
    private SlackProperties slackProperties;
    private ObjectMapper objectMapper;

    private final String slackId = "SLACK_ID";
    private final String slackEmail = "test@example.com";
    private final Long userId = 123L;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mappingRepository = mock(SlackUserMappingRepository.class);
        slackUserService = mock(SlackUserService.class);
        notificationHandler = mock(NotificationHandler.class);
        slackProperties = mock(SlackProperties.class);
        objectMapper = mock(ObjectMapper.class);

        slackAccountService = new SlackAccountService(
                userService,
                mappingRepository,
                slackUserService,
                notificationHandler,
                slackProperties,
                objectMapper
        );
    }

    @Test
    void testVerifyAndLinkAccount_validSlackUser_createsMappingAndSendsNotification() {
        User mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(mockUser);
        when(slackUserService.verifySlackUser(slackId)).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(mockUser);

        slackAccountService.verifyAndLinkAccount(userId, slackId, slackEmail);

        verify(mappingRepository).save(any(SlackUserMapping.class));
        verify(userService).save(any(User.class));
        verify(notificationHandler).sendNotification(any(NotificationMessage.class));
    }

    @Test
    void testVerifyAndLinkAccount_invalidSlackUser_throwsException() {
        when(slackUserService.verifySlackUser(slackId)).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () ->
                slackAccountService.verifyAndLinkAccount(userId, slackId, slackEmail)
        );
        assertEquals(ErrorStatus._SLACK_LINK_ERROR, exception.getErrorCode());
    }

    @Test
    void testUnlinkSlackAccount_successfulUnlink_deactivatesMappings() {
        User mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(mockUser);
        when(userService.save(any(User.class))).thenReturn(mockUser);

        slackAccountService.unlinkSlackAccount(userId);

        verify(mappingRepository).deactivateAllByUserId(userId);
        verify(userService).save(any(User.class));
    }

    @Test
    void testIsSlackLinked_withLinkedSlackAccount_returnsTrue() {
        User mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(userId);
        when(mockUser.isSlackLinked()).thenReturn(true);
        when(userService.findById(userId)).thenReturn(mockUser);
        when(mappingRepository.findByUserIdAndActiveTrue(userId))
                .thenReturn(Optional.of(mock(SlackUserMapping.class)));

        boolean result = slackAccountService.isSlackLinked(userId);

        assertTrue(result);
    }

    @Test
    void testIsSlackLinked_withUnlinkedSlackAccount_returnsFalse() {
        User mockUser = mock(User.class);
        lenient().when(mockUser.getId()).thenReturn(userId);
        when(mockUser.isSlackLinked()).thenReturn(false);
        when(userService.findById(userId)).thenReturn(mockUser);
        when(mappingRepository.findByUserIdAndActiveTrue(userId))
                .thenReturn(Optional.empty());

        boolean result = slackAccountService.isSlackLinked(userId);

        assertFalse(result);
    }

    @Test
    void testProcessSlackEvent_invalidSignature_returnsUnauthorized() {
        SlackProperties.App mockApp = mock(SlackProperties.App.class);
        when(slackProperties.getApp()).thenReturn(mockApp);
        when(mockApp.getSigningSecret()).thenReturn("mockSecret");

        ResponseEntity<?> response = slackAccountService.processSlackEvent(
                "{}",
                "invalid_signature",
                String.valueOf(System.currentTimeMillis() / 1000)
        );

        assertEquals(401, response.getStatusCodeValue());
    }
}