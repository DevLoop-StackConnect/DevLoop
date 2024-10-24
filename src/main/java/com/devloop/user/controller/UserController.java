package com.devloop.user.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.common.utils.S3Util;
import com.devloop.user.dto.response.UserResponse;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final S3Util s3Util;

    @PostMapping("/s3test")
    public String s3test(@RequestParam("file") MultipartFile file ) {
        System.out.println("들어옴");
        s3Util.uploadFile(file);
        return "성공";
    }

    @GetMapping("/v1/users/profiles")
    public ApiResponse<UserResponse> getUser(@AuthenticationPrincipal AuthUser authUser){
        return ApiResponse.ok(userService.getUser(authUser));
    }

    @PatchMapping("/v1/users/profiles/image")
    public ApiResponse<String>updateProfileImg(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal AuthUser authUser){
        userService.updateProfileImg(file, authUser);
        return ApiResponse.ok("프로필 이미지가 변경 되었습니다.");
    }
}
