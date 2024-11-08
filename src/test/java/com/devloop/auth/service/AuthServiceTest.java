package com.devloop.auth.service;

import com.devloop.auth.request.LoginRequest;
import com.devloop.auth.request.SignoutRequest;
import com.devloop.auth.request.SignupRequest;
import com.devloop.auth.response.SignupResponse;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.JwtUtil;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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


    /*@BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("testExample@example.com", "Qwer!234", "testUser", "ROLE_USER");
        user = User.of("testUser", "testExample@example.com", "encodedPassword", UserRole.ROLE_USER);
        user.setId(1L);
        loginRequest = new LoginRequest("testExample.com", "password");
        signoutRequest = new SignoutRequest("Qwer!234");
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
    void createUser_UserAlreadyExists() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        assertThrows(ApiException.class, () -> authService.createUser(signupRequest));
        //then
        verify(userRepository).findByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).thenReturn("jwtToken");
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
    void deleteUser_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(signoutRequest.getPassword(), user.getPassword())).thenReturn(true);

        // when
        authService.deleteUser(1L, signoutRequest);

        // then
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(signoutRequest.getPassword(), user.getPassword());
        verify(userRepository).save(user);
    }*/

}