package com.devloop.auth.controller;


import com.devloop.auth.request.LoginRequest;
import com.devloop.auth.request.SignoutRequest;
import com.devloop.auth.request.SignupRequest;
import com.devloop.auth.response.SignupResponse;
import com.devloop.auth.service.AuthService;
import com.devloop.auth.service.KakaoService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final KakaoService kakaoService;

    @PostMapping("/v1/auth/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.ok(authService.createUser(signupRequest));
    }

    //토큰 발급 방식에서는 ResponseEntity가 HTTP 표준이랑 보안적 측면으로 더 유용함
    @PostMapping("/v1/auth/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).header("Authorization", authService.login(loginRequest)).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_TUTOR')")
    @PutMapping("/v1/auth/signout")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody SignoutRequest signoutRequest) {
        Long id = authUser.getId();
        authService.deleteUser(id, signoutRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/auth/kakao/login")
    public ResponseEntity<Object> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", token)
                .build();
    }
}

