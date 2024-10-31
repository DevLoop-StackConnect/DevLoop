package com.devloop.lecturereview.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.service.LectureService;
import com.devloop.lecturereview.entity.LectureReview;
import com.devloop.lecturereview.repository.LectureReviewRepository;
import com.devloop.lecturereview.request.LectureReviewRequest;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureReviewService {

    private final LectureReviewRepository lectureReviewRepository;
    private final UserService userService;
    private final LectureService lectureService;

    //강의 후기 등록
    @Transactional
    public String saveLectureReview(AuthUser authUser, Long lectureId, @Valid LectureReviewRequest saveLectureReviewRequest) {
        //유저가 존재하는 지 확인
        User user=userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureService.findById(lectureId);

        //새로운 강의 후기 생성 및 저장
        LectureReview newLectureReview=LectureReview.from(saveLectureReviewRequest,user,lecture);
        lectureReviewRepository.save(newLectureReview);

        return String.format("%s 님의 댓글이 등록되었습니다", user.getUsername());
    }

    //강의 후기 수정
    @Transactional
    public String updateLectureReview(AuthUser authUser, Long lectureId, Long reviewId, @Valid LectureReviewRequest lectureReviewRequest) {
        //유저가 존재하는 지 확인
        User user=userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureService.findById(lectureId);

        //강의 후기가 존재하는 지 확인
        LectureReview lectureReview=lectureReviewRepository.findById(reviewId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE_REVIEW));

        //후기를 작성한 유저가 맞는 지 확인
        if(!authUser.getId().equals(lectureReview.getUser().getId())){
            throw new ApiException((ErrorStatus._PERMISSION_DENIED));
        }

        lectureReview.update(lectureReviewRequest);

        return String.format("%s 님의 댓글이 수정되었습니다",user.getUsername());
    }
}
