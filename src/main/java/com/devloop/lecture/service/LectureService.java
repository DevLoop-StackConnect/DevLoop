package com.devloop.lecture.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchResponseUtil;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.entity.QLecture;
import com.devloop.lecture.event.LectureCreatedEvent;
import com.devloop.lecture.event.LectureDeletedEvent;
import com.devloop.lecture.event.LectureUpdatedEvent;
import com.devloop.lecture.repository.jpa.LectureRepository;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecture.response.GetLectureDetailResponse;
import com.devloop.lecture.response.GetLectureListResponse;
import com.devloop.lecture.response.SaveLectureResponse;
import com.devloop.lecture.response.UpdateLectureResponse;
import com.devloop.lecturereview.entity.LectureReview;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final JPAQueryFactory queryFactory;

    //강의 등록 (유저의 권한이 TUTOR 일 경우에만 가능)
    @Transactional
    public SaveLectureResponse saveLecture(AuthUser authUser, SaveLectureRequest saveLectureRequest) {
        //유저가 존재하는 지 확인
        User user = userService.findByUserId(authUser.getId());

        //새로운 강의 생성
        Lecture newLecture = Lecture.from(saveLectureRequest, user);
        lectureRepository.save(newLecture);

        eventPublisher.publishEvent(new LectureCreatedEvent(newLecture));

        return SaveLectureResponse.of(newLecture.getId());
    }

    //강의 수정
    @Transactional
    public UpdateLectureResponse updateLecture(AuthUser authUser, Long lectureId, UpdateLectureRequest updateLectureRequest) {
        //유저가 존재하는 지 확인
        User user = userService.findByUserId(authUser.getId());

        //강의가 존재하는 지 확인
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() ->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의 등록한 유저 인지 확인
        if (!user.getId().equals(lecture.getUser().getId())) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //변경 사항 업데이트
        lecture.update(updateLectureRequest);

        //강의 승인여부 상태 변경
        lecture.changeApproval(Approval.WAITE);

        eventPublisher.publishEvent(new LectureUpdatedEvent(lecture));

        return UpdateLectureResponse.of(lecture.getId());
    }

    //강의 단건 조회
    public GetLectureDetailResponse getLecture(Long lectureId) {
        //강의가 존재하는 지 확인
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() ->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의가 승인이 되었는 지 확인
        if (!lecture.getApproval().equals(Approval.APPROVED)) {
            throw new ApiException(ErrorStatus._ACCESS_PERMISSION_DENIED);
        }

        //후기 평균 별점, 영상 총 수 포함
        return GetLectureDetailResponse.of(
                lecture.getTitle(),
                lecture.getUser().getUsername(),
                lecture.getDescription(),
                lecture.getRecommend(),
                lecture.getCategory().getDescription(),
                BoardType.LECTURE.getBoardType(),  // 수정된 부분: 직접 BoardType enum 사용
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
        PageRequest pageable = PageRequest.of(page - 1, size);

        Page<Lecture> lectures;

        //승인 된 강의만 조회
        if (title == null || title.isEmpty()) {
            lectures = lectureRepository.findByApproval(Approval.APPROVED, pageable);
        } else {
            lectures = lectureRepository.findByTitleContainingAndApproval(title, Approval.APPROVED, pageable);
        }
        return lectures.map(lecture -> GetLectureListResponse.of(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getCategory().getDescription(),
                BoardType.LECTURE.getBoardType(),
                lecture.getLevel().getLevel(),
                lecture.getPrice()
        ));
    }

    //강의 삭제
    @Transactional
    public void deleteLecture(AuthUser authUser, Long lectureId) {
        //유저가 존재하는 지 확인
        User user = userService.findByUserId(authUser.getId());

        //어드민인 지 확인
        boolean isAdminUser = user.getUserRole().equals(UserRole.ROLE_ADMIN);

        //강의가 존재하는 지 확인
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() ->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));

        //강의 등록한 유저 인지 확인
        if (!user.getId().equals(lecture.getUser().getId()) && !isAdminUser) {
            throw new ApiException(ErrorStatus._HAS_NOT_ACCESS_PERMISSION);
        }

        //강의 삭제ㅁ
        lectureRepository.delete(lecture);

        eventPublisher.publishEvent(new LectureDeletedEvent(lecture));
    }

    //Util
    public Lecture findById(Long lectureId) {
        return lectureRepository.findById(lectureId).orElseThrow(() ->
                new ApiException(ErrorStatus._NOT_FOUND_LECTURE));
    }

    public Page<Lecture> findAllWithPagination(PageRequest pageRequest) {
        try {
            Page<Lecture> lectures = lectureRepository.findAll(pageRequest);

            if (!lectures.isEmpty()) {
                List<Long> lectureIds = lectures.getContent()
                        .stream()
                        .map(Lecture::getId)
                        .collect(Collectors.toList());

                Map<Long, List<LectureVideo>> videoMap = lectureRepository.findWithVideosByIds(lectureIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Lecture::getId,
                                Lecture::getLectureVideos
                        ));

                Map<Long, List<LectureReview>> reviewMap = lectureRepository.findWithReviewsByIds(lectureIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Lecture::getId,
                                Lecture::getLectureReviews
                        ));

                lectures.getContent().forEach(lecture -> {
                    if (videoMap.containsKey(lecture.getId())) {
                        lecture.getLectureVideos().addAll(videoMap.get(lecture.getId()));
                    }
                    if (reviewMap.containsKey(lecture.getId())) {
                        lecture.getLectureReviews().addAll(reviewMap.get(lecture.getId()));
                    }
                });
            }
            return lectures;
        } catch (Exception e) {
            log.error("Error fetching lectures with pagination: ", e);
            throw new ApiException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public Page<IntegrationSearchResponse> getLectureWithPage(BooleanBuilder condition, PageRequest pageable) {
        QLecture qLecture = QLecture.lecture;

        List<Lecture> lectures = queryFactory
                .selectFrom(qLecture)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(qLecture.id.count())
                .from(qLecture)
                .where(condition)
                .fetchOne();

        List<IntegrationSearchResponse> response = SearchResponseUtil.wrapResponse(BoardType.LECTURE, lectures);
        return new PageImpl<>(response, pageable, total);
    }
}
