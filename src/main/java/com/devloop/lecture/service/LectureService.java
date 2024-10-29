package com.devloop.lecture.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserService userService;

    @Transactional
    public String saveLecture(AuthUser authUser, SaveLectureRequest saveLectureRequest) {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //요청한 유저의 권한이 USER일 경우 예외 처리
        if(user.getUserRole().equals(UserRole.ROLE_USER)){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //새로운 강의 생성
        Lecture newLecture=Lecture.from(saveLectureRequest, user);
        lectureRepository.save(newLecture);

        return String.format("%s 님의 강의가 등록 완료되었습니다. 승인까지 3~5일 정도 소요될 수 있습니다.", user.getUsername());
    }


}
