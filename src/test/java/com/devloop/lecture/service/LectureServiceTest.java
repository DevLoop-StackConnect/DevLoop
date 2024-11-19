package com.devloop.lecture.service;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.community.event.CommunityDeletedEvent;
import com.devloop.lecture.entity.Lecture;
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
import com.devloop.pwt.enums.Level;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {
    @InjectMocks
    private LectureService lectureService;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AuthUser authUser;
    private User user;
    private SaveLectureRequest saveLectureRequest;
    private Lecture lecture;

    @BeforeEach
    void setUp() throws Exception {
        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_TUTOR);
        user = User.of("홍길동", "Abc123!", "test@email.com", UserRole.ROLE_TUTOR);

        Constructor<SaveLectureRequest> constructor = SaveLectureRequest.class.getDeclaredConstructor(
                String.class, String.class, String.class, Category.class, Level.class, BigDecimal.class
        );
        constructor.setAccessible(true);

        saveLectureRequest = constructor.newInstance(
                "테스트 강의",
                "설명",
                "추천인",
                Category.WEB_DEV,
                Level.EASY,
                new BigDecimal(100000)
        );

        lecture = Lecture.from(saveLectureRequest, user);
        Field lectureField = Lecture.class.getDeclaredField("approval");
        lectureField.setAccessible(true);
        lectureField.set(lecture, Approval.APPROVED);

        Field lectureVideosField = Lecture.class.getDeclaredField("lectureVideos");
        lectureVideosField.setAccessible(true);
        lectureVideosField.set(lecture, new ArrayList<>());

        Field lectureReviewsField = Lecture.class.getDeclaredField("lectureReviews");
        lectureReviewsField.setAccessible(true);
        lectureReviewsField.set(lecture, new ArrayList<>());
    }

    @Test
    void 강의_등록_성공() {
        //given
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(lectureRepository.save(any())).willReturn(lecture);

        //when
        SaveLectureResponse saveLectureResponse = lectureService.saveLecture(authUser, saveLectureRequest);

        //then
        Assertions.assertNotNull(saveLectureResponse);
        Assertions.assertEquals(saveLectureResponse.getLectureId(), lecture.getId());
        Assertions.assertEquals(lecture.getApproval(), Approval.APPROVED);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(LectureCreatedEvent.class));
    }

    @Test
    void 강의_수정_성공() throws Exception {
        //given
        Long lectureId = 1L;
        user.setId(1L);
        Constructor<UpdateLectureRequest> constructor = UpdateLectureRequest.class.getDeclaredConstructor(
                String.class, String.class, String.class, Category.class, Level.class, BigDecimal.class
        );
        constructor.setAccessible(true);

        UpdateLectureRequest updateLectureRequest = constructor.newInstance(
                "수정된 강의",
                "수정된 설명",
                "수정된 추천인",
                Category.APP_DEV,
                Level.MEDIUM,
                new BigDecimal(200000)
        );

        given(userService.findByUserId(anyLong())).willReturn(user);
        given(lectureRepository.findById(anyLong())).willReturn(Optional.ofNullable(lecture));

        //when
        UpdateLectureResponse updateLectureResponse = lectureService.updateLecture(authUser, lectureId, updateLectureRequest);

        //then
        Assertions.assertNotNull(updateLectureResponse);
        Assertions.assertEquals(lecture.getTitle(), "수정된 강의");
        Assertions.assertEquals(lecture.getPrice(), new BigDecimal(200000));
        Assertions.assertEquals(lecture.getCategory(), Category.APP_DEV);
        Assertions.assertEquals(lecture.getApproval(), Approval.WAITE);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(LectureUpdatedEvent.class));
    }

    @Test
    void 강의_단건_조회_성공() throws Exception {
        //given
        Long lectureId = 1L;
        given(lectureRepository.findById(anyLong())).willReturn(Optional.ofNullable(lecture));

        //when
        GetLectureDetailResponse lectureDetailResponse = lectureService.getLecture(lectureId);

        //then
        Assertions.assertNotNull(lectureDetailResponse);
        Assertions.assertEquals(lectureDetailResponse.getTitle(), "테스트 강의");
        Assertions.assertEquals(lectureDetailResponse.getTutorName(), "홍길동");
        Assertions.assertEquals(lectureDetailResponse.getDescription(), "설명");
        Assertions.assertEquals(lectureDetailResponse.getVideoCount(), 0);
        Assertions.assertEquals(lectureDetailResponse.getReviewCount(), 0);
        Assertions.assertEquals(lectureDetailResponse.getPrice(), new BigDecimal(100000));
        Assertions.assertEquals(lectureDetailResponse.getCategory(), Category.WEB_DEV.getDescription());
    }

    @Test
    void 승인안됨_강의_단건_조회_예외() throws Exception {
        //given
        Long lectureId = 1L;
        given(lectureRepository.findById(anyLong())).willReturn(Optional.ofNullable(lecture));

        Field lectureField = Lecture.class.getDeclaredField("approval");
        lectureField.setAccessible(true);
        lectureField.set(lecture, Approval.WAITE);

        //when & then
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> lectureService.getLecture(lectureId));

        Assertions.assertEquals(exception.getErrorCode(), ErrorStatus._ACCESS_PERMISSION_DENIED);
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
        given(lectureRepository.findByTitleContainingAndApproval(title, Approval.APPROVED, pageable)).willReturn(lectures);

        //when
        Page<GetLectureListResponse> lectureListResponsePage = lectureService.getLectureList(title, page, size);

        //then
        Assertions.assertNotNull(lectureListResponsePage);
        Assertions.assertEquals(1, lectureListResponsePage.getTotalElements());
        Assertions.assertEquals(lecture.getTitle(), lectureListResponsePage.getContent().get(0).getTitle());
        Assertions.assertEquals(lecture.getApproval(), Approval.APPROVED);
    }

    @Test
    void 강의_삭제_성공() {
        //given
        Long lectureId = 1L;
        user.setId(1L);
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(lectureRepository.findById(anyLong())).willReturn(Optional.ofNullable(lecture));

        //when
        lectureService.deleteLecture(authUser, lectureId);

        //then
        verify(lectureRepository, times(1)).delete(lecture);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(LectureDeletedEvent.class));
    }

    @Test
    void 어드민_강의_삭제_성공() throws Exception {
        //given
        Long lectureId = 1L;
        user.setId(1L);
        authUser = new AuthUser(2L, "test@email.com", UserRole.ROLE_ADMIN);
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(lectureRepository.findById(anyLong())).willReturn(Optional.ofNullable(lecture));

        //when
        lectureService.deleteLecture(authUser, lectureId);

        //then
        verify(lectureRepository, times(1)).delete(lecture);
    }
}