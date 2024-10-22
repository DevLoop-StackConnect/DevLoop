package com.devloop.auth.controller;


import com.devloop.auth.request.LoginRequest;
import com.devloop.auth.request.SignoutRequest;
import com.devloop.auth.request.SignupRequest;
import com.devloop.auth.response.SignupResponse;
import com.devloop.auth.service.AuthService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.ok(authService.createUser(signupRequest));
    }

    @PostMapping("/v1/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).header("Authorization",authService.login(loginRequest)).build();
    }

    @PutMapping("/v1/signout")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal AuthUser authUser, @RequestBody SignoutRequest signoutRequest) {
        Long id = authUser.getId();
        authService.deleteUser(id, signoutRequest);
        return ResponseEntity.noContent().build();
    }
}