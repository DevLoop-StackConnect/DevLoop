package com.devloop.lecture.service;

import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.enums.VideoStatus;
import com.devloop.lecture.repository.LectureRepository;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.response.GetLectureDetailResponse;
import com.devloop.lecture.response.GetLectureListResponse;
import com.devloop.pwt.enums.Level;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LectureAdminServiceTest {
    @InjectMocks
    private LectureAdminService lectureAdminService;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureVideoService lectureVideoService;

    @Mock
    private SaveLectureRequest saveLectureRequest;
    private Lecture lecture;
    private AuthUser authUser;
    private User user;
    private LectureVideo lectureVideo;
    private List<LectureVideo> lectureVideos;

    @BeforeEach
    void setUp() throws Exception {
        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_ADMIN);
        user = User.of("홍길동", "Abc123!", "test@email.com", UserRole.ROLE_ADMIN);

        //강의
        Constructor<SaveLectureRequest> lectureConstructor = SaveLectureRequest.class.getDeclaredConstructor(
                String.class, String.class, String.class, Category.class, Level.class, BigDecimal.class
        );
        lectureConstructor.setAccessible(true);
        saveLectureRequest = lectureConstructor.newInstance(
                "테스트 강의",
                "설명",
                "추천인",
                Category.WEB_DEV,
                Level.EASY,
                new BigDecimal(100000)
        );
        lecture = Lecture.from(saveLectureRequest, user);

        //강의 영상
        Constructor<LectureVideo> videoConstructor = LectureVideo.class.getDeclaredConstructor(
                String.class, String.class, VideoStatus.class, FileFormat.class, Lecture.class
        );
        videoConstructor.setAccessible(true);
        lectureVideo = videoConstructor.newInstance(
                "테스트 영상",
                "제목",
                VideoStatus.COMPLETED,
                FileFormat.MP4,
                lecture
        );
        lectureVideos = Arrays.asList(lectureVideo);

        Field lectureVideosField = Lecture.class.getDeclaredField("lectureVideos");
        lectureVideosField.setAccessible(true);
        lectureVideosField.set(lecture, lectureVideos);

        //강의 리뷰
        Field lectureReviewsField = Lecture.class.getDeclaredField("lectureReviews");
        lectureReviewsField.setAccessible(true);
        lectureReviewsField.set(lecture, new ArrayList<>());
    }

    @Test
    void 강의_승인_성공() {
        //given
        Long lectureId = 1L;

        given(lectureRepository.findById(any())).willReturn(Optional.ofNullable(lecture));
        given(lectureVideoService.findLectureVideoByLectureId(any())).willReturn(lectureVideos);

        //when
        String result = lectureAdminService.changeApproval(lectureId);

        //then
        Assertions.assertEquals("테스트 강의 강의가 승인 되었습니다.", result);
        Assertions.assertEquals(lecture.getApproval(), Approval.APPROVED);
    }

    @Test
    void 영상_없을_때_강의_승인_실패() {
        //given
        Long lectureId = 1L;
        given(lectureRepository.findById(any())).willReturn(Optional.ofNullable(lecture));
        given(lectureVideoService.findLectureVideoByLectureId(any())).willReturn(Collections.emptyList());

        //when & then
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> lectureAdminService.changeApproval(lectureId));

        Assertions.assertEquals(exception.getErrorCode(), ErrorStatus._INVALID_LECTURE_VIDEO);
    }

    @Test
    void 강의_단건_조회_성공() {
        //given
        Long lectureId = 1L;
        given(lectureRepository.findById(any())).willReturn(Optional.ofNullable(lecture));

        //when
        GetLectureDetailResponse getLectureDetailResponse = lectureAdminService.getLecture(lectureId);

        //then
        Assertions.assertNotNull(getLectureDetailResponse);
        Assertions.assertEquals(getLectureDetailResponse.getTitle(), "테스트 강의");
        Assertions.assertEquals(getLectureDetailResponse.getTutorName(), "홍길동");
        Assertions.assertEquals(getLectureDetailResponse.getDescription(), "설명");
        Assertions.assertEquals(getLectureDetailResponse.getVideoCount(), 1);
        Assertions.assertEquals(getLectureDetailResponse.getReviewCount(), 0);
        Assertions.assertEquals(getLectureDetailResponse.getPrice(), new BigDecimal(100000));
        Assertions.assertEquals(getLectureDetailResponse.getCategory(), Category.WEB_DEV.getDescription());
    }

    @Test
    void 강의_다건_조회_성공() {
        //given
        String title = "테스트";
        int page = 1;
        int size = 10;
        PageRequest pageable = PageRequest.of(page - 1, size);

        List<Lecture> lectureList = List.of(lecture);
        Page<Lecture> lectures = new PageImpl<>(List.of(lecture), pageable, lectureList.size());
        given(lectureRepository.findByTitleContainingAndApproval(title, Approval.WAITE, pageable)).willReturn(lectures);

        //when
        Page<GetLectureListResponse> lectureListResponsePage = lectureAdminService.getLectureList(title, page, size);

        //then
        Assertions.assertNotNull(lectureListResponsePage);
        Assertions.assertEquals(1, lectureListResponsePage.getTotalElements());
        Assertions.assertEquals(lecture.getTitle(), lectureListResponsePage.getContent().get(0).getTitle());
        Assertions.assertEquals(lecture.getApproval(), Approval.WAITE);
    }
}