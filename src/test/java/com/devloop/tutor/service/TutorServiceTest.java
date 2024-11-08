package com.devloop.tutor.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.tutor.request.TutorRequestSaveRequest;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TutorServiceTest {

    @Mock
    private TutorRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TutorService tutorService;

    private AuthUser authUser;
    private Object instance;
    private User user;
    private TutorRequest tutorRequest;

    @BeforeEach
    void setUp() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // given
        authUser = new AuthUser(1L, "test@test.com", UserRole.ROLE_TUTOR);

        Class<?> request = Class.forName("com.devloop.tutor.request.TutorRequestSaveRequest");
        Constructor<?> constructor = request.getDeclaredConstructor(String.class, String.class, String.class);
        constructor.setAccessible(true);
        instance = constructor.newInstance("홍길동", "http://github.com/test", "123-4567-4567");

        user = User.of("홍길동", "test@test.com", "hong1234!", UserRole.ROLE_TUTOR);
        tutorRequest = TutorRequest.of("홍길동", "http://github.com/test", "123-4567-4567", user);
    }

    @Test
    void 튜터_신청_저장_성공() {
        // given
        when(userService.findByUserId(authUser.getId())).thenReturn(user);
        when(requestRepository.existsByUserId(user.getId())).thenReturn(false);
        when(requestRepository.save(any(TutorRequest.class))).thenReturn(tutorRequest);

        // when
        String response = tutorService.saveTutorRequest(authUser, (TutorRequestSaveRequest) instance);

        // then
        assertEquals("홍길동 님의 튜터 신청이 정삭적으로 요청처리 되었습니다.승인까지 3~5일 정도 소요될 수 있습니다.", response);
        verify(requestRepository, times(1)).save(any(TutorRequest.class));
    }

    @Test
    void 튜터_신청_내역_이미존재하여_실패() {

        // when
        when(userService.findByUserId(authUser.getId())).thenReturn(user);
        when(requestRepository.existsByUserId(user.getId())).thenReturn(true);

        // then
        ApiException exception = assertThrows(ApiException.class, () -> {
            tutorService.saveTutorRequest(authUser, (TutorRequestSaveRequest) instance);
        });

        assertEquals(ErrorStatus._TUTOR_REQUEST_ALREADY_EXIST, exception.getErrorCode());
    }

    @Test
    void 튜터_신청_내역_가져오기_성공(){
        // given
        when(requestRepository.findByUserId(user.getId())).thenReturn(Optional.of(tutorRequest));

        // when
        TutorRequest result = tutorService.getTutorRequestByUserId(user.getId());

        // then
        assertNotNull(result);
        assertEquals(tutorRequest, result);
    }

    @Test
    void 튜터_신청_내역_가져오기_실패(){
        // given
        when(requestRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // when
        TutorRequest result = tutorService.getTutorRequestByUserId(user.getId());

        // then
        assertNull(result);
    }

}