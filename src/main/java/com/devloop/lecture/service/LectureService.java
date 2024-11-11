package com.devloop.lecture.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchResponseUtil;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.QLecture;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecture.response.GetLectureDetailResponse;
import com.devloop.lecture.response.GetLectureListResponse;
import com.devloop.lecture.response.SaveLectureResponse;
import com.devloop.lecture.response.UpdateLectureResponse;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.user.entity.User;
import com.devloop.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserService userService;
    private final JPAQueryFactory queryFactory;

    //강의 등록 (유저의 권한이 TUTOR 일 경우에만 가능)
    @Transactional
    public SaveLectureResponse saveLecture(AuthUser authUser, SaveLectureRequest saveLectureRequest) {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //새로운 강의 생성
        Lecture newLecture=Lecture.from(saveLectureRequest, user);
        lectureRepository.save(newLecture);

        return SaveLectureResponse.of(newLecture.getId());
    }

    //강의 수정
    @Transactional
    public UpdateLectureResponse updateLecture(AuthUser authUser, Long lectureId, UpdateLectureRequest updateLectureRequest) {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의 등록한 유저 인지 확인
        if(!user.getId().equals(lecture.getUser().getId())){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //변경 사항 업데이트
        lecture.update(updateLectureRequest);

        //강의 승인여부 상태 변경
        lecture.changeApproval(Approval.WAITE);

        return UpdateLectureResponse.of(lecture.getId());
    }

    //강의 단건 조회
    public GetLectureDetailResponse getLecture(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의가 승인이 되었는 지 확인
        if(!lecture.getApproval().equals(Approval.APPROVED)){
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        //후기 평균 별점, 영상 총 수 포함
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

    //강의 다건 조회
    public Page<GetLectureListResponse> getLectureList(String title, int page, int size) {
        PageRequest pageable= PageRequest.of(page-1,size);

        Page<Lecture> lectures;

        //승인 된 강의만 조회
        if(title==null || title.isEmpty()){
            lectures=lectureRepository.findByApproval(Approval.APPROVED,pageable);
        }else{
            lectures=lectureRepository.findByTitleContainingAndApproval(title,Approval.APPROVED,pageable);
        }
        return lectures.map(lecture-> GetLectureListResponse.of(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getCategory().getDescription(),
                lecture.getLevel().getLevel(),
                lecture.getPrice()
        ));
    }

    //강의 삭제
    @Transactional
    public void deleteLecture(AuthUser authUser, Long lectureId) {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의 등록한 유저 인지 확인
        if(!user.getId().equals(lecture.getUser().getId())){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //강의 삭제
        lectureRepository.delete(lecture);
    }

    //Util
    public Lecture findById(Long lectureId){
        return lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));
    }

    public Page<IntegrationSearchResponse> getLectureWithPage(BooleanBuilder condition, PageRequest pageable) {
        QLecture qLecture = QLecture.lecture;

        // QueryDSL로 조건에 맞는 Lecture 페이지 조회
        List<Lecture> lectures = queryFactory
                .selectFrom(qLecture)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 요소 수 계산 (명확한 경로 지정)
        long total = queryFactory
                .select(qLecture.id.count())  // id의 count를 사용
                .from(qLecture)
                .where(condition)
                .fetchOne();

        // IntegrationSearchResponse로 변환하여 반환
        List<IntegrationSearchResponse> response = lectures.stream()
                .map(lecture -> IntegrationSearchResponse.of(lecture, BoardType.LECTURE.name().toLowerCase()))
                .collect(Collectors.toList());

        return new PageImpl<>(response, pageable, total);
    }
}
