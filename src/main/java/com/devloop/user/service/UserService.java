package com.devloop.user.service;

import com.devloop.common.AuthUser;
import com.devloop.common.utils.S3Util;
import com.devloop.user.dto.response.UserResponse;
import com.devloop.user.entity.User;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public UserResponse getUser(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new NoSuchElementException("User not found"));
        return UserResponse.from(user.getUsername(),user.getEmail(),user.getUserRole());
    }

    @Transactional
    public void updateProfileImg(MultipartFile file, AuthUser authUser) {
        s3Util.uploadFile(file);
        User user = userRepository.findById(authUser.getId()).orElseThrow(()->new NoSuchElementException("User not found"));
        user.updateProfileImg(3L);
    }
}
