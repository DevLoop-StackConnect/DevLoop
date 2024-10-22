package com.devloop.user.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.common.utils.S3Util;
import com.devloop.user.dto.response.UserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public ApiResponse<UserResponse> getUser(AuthUser authUser) {
        // User.fromUser 로 바꿀지 고민중...
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new NoSuchElementException("User not found"));
        UserResponse userResponse = new UserResponse(user.getUsername(),user.getEmail(),user.getUserRole());
        return ApiResponse.ok(userResponse);
    }

    @Transactional
    public void updateProflieImg(MultipartFile file, AuthUser authUser) {
        s3Util.uploadFile(file,bucketName);
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new NoSuchElementException("User not found"));
        user.updateProfileImg(3L);
    }
}
