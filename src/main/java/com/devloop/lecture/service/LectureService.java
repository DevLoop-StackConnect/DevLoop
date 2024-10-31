package com.devloop.lecture.service;

import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecture.response.LectureDetailResponse;
import com.devloop.lecture.response.LectureListResponse;
import com.devloop.lecture.response.SaveLectureResponse;
import com.devloop.lecture.response.UpdateLectureResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserService userService;
    private final LectureVideoService lectureVideoService;
    private final S3Service s3Service;

    //강의 등록 (유저의 권한이 TUTOR일 경우에만 가능)
    @Transactional
    public SaveLectureResponse saveLecture(AuthUser authUser, SaveLectureRequest saveLectureRequest) {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //요청한 유저의 권한이 TUTOR인지 확인
        if(user.getUserRole().equals(UserRole.ROLE_USER)){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

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
    public LectureDetailResponse getLecture(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의가 승인이 되었는 지 확인
        if(!lecture.getApproval().equals(Approval.APPROVED)){
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        //후기 평균 별점, 영상 총 수 포함
        return LectureDetailResponse.of(
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getRecommend(),
                lecture.getCategory().getDescription(),
                lecture.getLevel().getLevel(),
                lecture.getPrice(),
                lecture.getCreatedAt(),
                lecture.getModifiedAt()
        );
    }

    //강의 다건 조회
    public Page<LectureListResponse> getLectureList(String title, int page, int size) {
        PageRequest pageable= PageRequest.of(page-1,size);

        Page<Lecture> lectures;

        if(title==null || title.isEmpty()){
            lectures=lectureRepository.findAll(pageable);
        }else{
            lectures=lectureRepository.findByTitleContaining(title,pageable);
        }
        return lectures.map(lecture->LectureListResponse.of(
                lecture.getTitle(),
                lecture.getCategory().getDescription(),
                lecture.getLevel().getLevel(),
                lecture.getPrice()
        ));
    }

    //강의 삭제
    @Transactional
    public String deleteLecture(AuthUser authUser, Long lectureId) {
        //유저가 존재하는 지 확인
        User user= userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture=lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의 등록한 유저 인지 확인
        if(!user.getId().equals(lecture.getUser().getId())){
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //영상리스트 있는 지 확인 및 삭제
        Optional<List<LectureVideo>> lectureVideoList=lectureVideoService.findLectureVideoByLectureId(lectureId);

        lectureVideoList.ifPresent(lectureVideos -> lectureVideos.forEach(video -> {
            //S3 영상 파일 삭제
            s3Service.delete(video.getFileName());
            //데이터베이스에서 삭제
            lectureVideoService.deleteLectureVideo(video);
        }));

        //강의 삭제
        lectureRepository.delete(lecture);

        return String.format("%s 강의를 삭제하였습니다.", lecture.getTitle());
    }

    //Util
    public Lecture findById(Long lectureId){
        return lectureRepository.findById(lectureId).orElseThrow(()->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));
    }


}
