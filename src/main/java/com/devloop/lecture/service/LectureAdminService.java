package com.devloop.lecture.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.response.LectureDetailResponse;
import com.devloop.lecture.response.LectureListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureAdminService {

    private final LectureRepository lectureRepository;

    //강의 승인 (ADMIN)
    @Transactional
    public String changeApproval(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_Lecture));

        //강의 승인여부 상태 변경
        lecture.changeApproval(Approval.APPROVED);

        return String.format("%s 강의가 승인 되었습니다.", lecture.getTitle());
    }

//    //강의 단건 조회 (ADMIN)
//    public LectureDetailResponse getLecture(Long lectureId) {
//        //강의가 존재하는 지 확인
//        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
//                new ApiException(ErrorStatus._NOT_FOUND_Lecture));
//
//        //강의 영상 가져오기
//
//    }
//
//    //승인되지 않은 강의 다건 조회 (ADMIN)
//    public Page<LectureListResponse> getLectureList(int page, int size) {
//        Pageable pageable= PageRequest.of(page-1,size);
//
//
//    }
}
