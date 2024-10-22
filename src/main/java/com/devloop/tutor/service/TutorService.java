package com.devloop.tutor.service;

import com.devloop.common.AuthUser;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.tutor.request.TutorRequestSaveRequest;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorService {

    private final TutorRequestRepository requestRepository;
    private final UserRepository userRepository;

    // 튜터 신청
    @Transactional
    public String saveTutorRequest(
            AuthUser authUser,
            TutorRequestSaveRequest tutorRequest
    ) {
        // 사용자 객체 가져오기
        User requestUser = userRepository.findById(authUser.getId()).orElseThrow();

        // 요청한 사용자의 권한이 USER인지 확인
        if(requestUser.getUserRole().equals(UserRole.USER))


        return String.format("%s 님의 튜터 신청이 정삭적으로 요청처리 되었습니다.\n" +
                "승인까지 3~5일 정도 소요될 수 있습니다.", authUser.getEmail());
    }
}
