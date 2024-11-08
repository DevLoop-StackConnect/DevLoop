package com.devloop.tutor.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.tutor.response.TutorRequestListAdminResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorAdminServiceTest {

    @Mock
    private TutorRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TutorAdminService tutorAdminService;

    private TutorRequest tutorRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("홍길동", "test@test.com", "hong1234!", UserRole.ROLE_USER);
        tutorRequest = TutorRequest.of("홍길동", "http://github.com/test", "123-4567-4567", user);
        tutorRequest.changeStatus(Approval.WAITE);
    }

    @Test
    void 튜터_신청_요청_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TutorRequest> requestPage = new PageImpl<>(Collections.singletonList(tutorRequest), pageable, 1);

        when(requestRepository.findAllByStatus(pageable, Approval.WAITE)).thenReturn(requestPage);

        // when
        Page<TutorRequestListAdminResponse> responses = tutorAdminService.getAllTutorRequest(1, 10);

        // then
        assertEquals(1, responses.getTotalElements());
        TutorRequestListAdminResponse response = responses.getContent().get(0);
        assertEquals("홍길동", response.getName());
        assertEquals("http://github.com/test", response.getSubUrl());
    }

    @Test
    void 튜터_신청_요청_내역_없어_조회_실패(){
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TutorRequest> emptyRequestPage = Page.empty(pageable);

        // when
        when(requestRepository.findAllByStatus(pageable, Approval.WAITE)).thenReturn(emptyRequestPage);

        // then
        ApiException exception = assertThrows(ApiException.class, () -> {tutorAdminService.getAllTutorRequest(1, 10);});
        assertEquals(ErrorStatus._TUTOR_REQUEST_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    void 튜터_신청_승인_성공(){
        // given
        Long userId = 1L;
        when(userService.findByUserId(userId)).thenReturn(user);
        when(requestRepository.findByUserId(userId)).thenReturn(Optional.of(tutorRequest));

        // when
        String response = tutorAdminService.changeUserRoleToTutor(userId);

        // then
        assertEquals("홍길동 님의 튜터 신청이 승인되었습니다.", response);
        assertEquals(UserRole.ROLE_TUTOR, user.getUserRole());
        assertEquals(Approval.APPROVED, tutorRequest.getStatus());
    }

    @Test
    void 튜터_신청_승인_내역_없어_실패(){
        // given
        Long userId = 1L;
        when(userService.findByUserId(userId)).thenReturn(user);
        when(requestRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> {tutorAdminService.changeUserRoleToTutor(userId);});
        assertEquals(ErrorStatus._TUTOR_REQUEST_NOT_EXIST, exception.getErrorCode());
    }
}