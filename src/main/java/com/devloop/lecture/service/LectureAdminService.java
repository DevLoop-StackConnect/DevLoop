package com.devloop.lecture.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.response.GetLectureDetailResponse;
import com.devloop.lecture.response.GetLectureListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureAdminService {

    private final LectureRepository lectureRepository;
    private final LectureVideoService lectureVideoService;

    //강의 승인 (ADMIN)
    @Transactional
    public String changeApproval(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의 영상이 존재하는 지 확인
        List<LectureVideo> lectureVideo=lectureVideoService.findLectureVideoByLectureId(lectureId);
        if(lectureVideo.isEmpty()){
            throw new ApiException(ErrorStatus._INVALID_LECTURE_VIDEO);
        }

        //강의 승인여부 상태 변경
        lecture.changeApproval(Approval.APPROVED);

        return String.format("%s 강의가 승인 되었습니다.", lecture.getTitle());
    }

    //강의 단건 조회 (ADMIN)
    public GetLectureDetailResponse getLecture(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //후기 평균 별점 - 필요x, 영상 총 수
        return GetLectureDetailResponse.of(
                lecture.getTitle(),
                lecture.getUser().getUsername(),
                lecture.getDescription(),
                lecture.getRecommend(),
                lecture.getCategory().getDescription(),
                lecture.getLevel().getLevel(),
                lecture.getPrice(),
                lecture.getLectureVideos().size(),
                lecture.getLectureReviews().size(),
                lecture.getCreatedAt(),
                lecture.getModifiedAt()
        );
    }

    //강의 다건 조회 (승인이 안된 강의만 조회) (ADMIN)
    public Page<GetLectureListResponse> getLectureList(String title,int page, int size) {
        PageRequest pageable= PageRequest.of(page-1,size);

        Page<Lecture> lectures;

        //승인 된 강의만 조회
        if(title==null || title.isEmpty()){
            lectures=lectureRepository.findByApproval(Approval.WAITE,pageable);
        }else{
            lectures=lectureRepository.findByTitleContainingAndApproval(title,Approval.WAITE,pageable);
        }
        return lectures.map(lecture-> GetLectureListResponse.of(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getCategory().getDescription(),
                lecture.getLevel().getLevel(),
                lecture.getPrice()
        ));
    }
}
