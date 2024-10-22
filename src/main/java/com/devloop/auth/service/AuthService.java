package com.devloop.auth.service;


import com.devloop.auth.request.LoginRequest;
import com.devloop.auth.request.SignoutRequest;
import com.devloop.auth.request.SignupRequest;
import com.devloop.auth.response.SignupResponse;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.JwtUtil;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.enums.UserStatus;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {


    private final BCryptPasswordEncoder passwordEncoders;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @Transactional
    public SignupResponse createUser(SignupRequest signupRequest) {

        String encodedPassword = passwordEncoders.encode(signupRequest.getPassword());
        Optional<User> existingUser = userRepository.findByEmail(signupRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new ApiException(ErrorStatus._INVALID_REQUEST);
        }

        User user = User.from(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encodedPassword,
                UserRole.of(signupRequest.getRole()));
        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getCreatedAt()
        );
    }

    public String login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->new ApiException(ErrorStatus._NOT_FOUND_USER));

        if(!passwordEncoders.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus._PERMISSION_DENIED);
        }

        if(user.getStatus() == UserStatus.WITHDRAWAL){
            throw new ApiException(ErrorStatus._NOT_FOUND_USER);
        }

        return jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );
    }

    @Transactional
    public void deleteUser(Long id, SignoutRequest signoutRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->new ApiException(ErrorStatus._NOT_FOUND_USER));

        if(passwordEncoders.matches(signoutRequest.getPassword(), user.getPassword())){
            user.update();
            userRepository.save(user);
        } else throw new ApiException(ErrorStatus._PERMISSION_DENIED);
    }
}