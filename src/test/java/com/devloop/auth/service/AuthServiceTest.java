package com.devloop.auth.service;

import com.devloop.auth.request.LoginRequest;
import com.devloop.auth.request.SignoutRequest;
import com.devloop.auth.request.SignupRequest;
import com.devloop.auth.response.SignupResponse;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.JwtUtil;
import com.devloop.user.entity.User;
import com.devloop.user.enums.LoginType;
import com.devloop.user.enums.UserRole;
import com.devloop.user.enums.UserStatus;
import com.devloop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private AuthService authService;
    private SignupRequest signupRequest;
    private User user;
    private LoginRequest loginRequest;
    private SignoutRequest signoutRequest;


    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
                .email("testExample@example.com")
                .password("Qwer!234")
                .username("testUser")
                .role(UserRole.ROLE_USER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("testExample@example.com")
                .password("password")
                .build();

        // SignoutRequest는 password만 필요
        signoutRequest = SignoutRequest.builder()
                .password("password")
                .build();

        user = User.of(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                "encodedPassword",
                signupRequest.getRole()
        );
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    void createUser_Success() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        SignupResponse signupResponse = authService.createUser(signupRequest);

        //then
        assertNotNull(signupResponse);
        assertEquals(user.getId(), signupResponse.getId());
        assertEquals(user.getEmail(), signupResponse.getEmail());
        assertEquals(user.getUsername(), signupResponse.getName());
        assertEquals(user.getCreatedAt(), signupResponse.getCreatedAt());

        verify(userRepository).findByEmail(signupRequest.getEmail());
        verify(passwordEncoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 회원가입_성공() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        SignupResponse response = authService.createUser(signupRequest);

        //then
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());

        verify(userRepository).findByEmail(signupRequest.getEmail());
        verify(passwordEncoder).encode(signupRequest.getPassword());
    }

    @Test
    void 회원가입_이메일중복_실패() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //when
        //중복이 되니 에러 출력
        ApiException exception = assertThrows(ApiException.class, () -> authService.createUser(signupRequest));
        assertEquals(ErrorStatus._INVALID_REQUEST, exception.getErrorCode());

        verify(userRepository).findByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 로그인_성공() {
        //given
        //유저 확인, 패스워드 인코드 확인, 토큰 생성 확인
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.createToken(eq(1L), eq("testExample@example.com"), eq(UserRole.ROLE_USER))).thenReturn("jwtToken");

        //when
        String token = authService.login(loginRequest);

        //then
        assertNotNull(token);
        assertEquals("jwtToken", token);

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtUtil).createToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    @Test
    void 로그인_사용자없음_실패() {
        //사용자 조회 -> 시실패 Exception
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //when
        ApiException exception = assertThrows(ApiException.class, () -> authService.login(loginRequest));
        assertEquals(ErrorStatus._NOT_FOUND_USER, exception.getErrorCode());

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any(UserRole.class));
    }

    @Test
    void 로그인_비밀번호_불일치(){
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //whne
        ApiException exception = assertThrows(ApiException.class, () -> authService.login(loginRequest));

        assertEquals(ErrorStatus._PERMISSION_DENIED, exception.getErrorCode());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtUtil, never()).createToken(anyLong(),anyString(), any(UserRole.class));
    }

    @Test
    void 로그인_탈퇴한사용자() {
        //given
        ReflectionTestUtils.setField(user, "status", UserStatus.WITHDRAWAL);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //when
        ApiException exception = assertThrows(ApiException.class, () -> authService.login(loginRequest));

        assertEquals(ErrorStatus._PERMISSION_DENIED, exception.getErrorCode());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(anyString(), anyString());
        verify(jwtUtil, never()).createToken(anyLong(),anyString(), any(UserRole.class));
    }

    @Test
    void 회원탈퇴_성공() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        authService.deleteUser(1L, signoutRequest);

        //then
        assertEquals(UserStatus.WITHDRAWAL, user.getStatus());
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(signoutRequest.getPassword(), user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void 회원탈퇴_사용자없음_실패() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(ApiException.class, () ->
                authService.deleteUser(1L, signoutRequest)
        );

        assertEquals(ErrorStatus._NOT_FOUND_USER, exception.getErrorCode());
        verify(userRepository).findById(1L);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}