package com.devloop.lecturereview.service;

import com.devloop.common.AuthUser;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.service.LectureService;
import com.devloop.lecturereview.entity.LectureReview;
import com.devloop.lecturereview.repository.LectureReviewRepository;
import com.devloop.lecturereview.request.SaveLectureReviewRequest;
import com.devloop.lecturereview.response.GetLectureReviewResponse;
import com.devloop.purchase.service.PurchaseService;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class LectureReviewServiceTest {
    @InjectMocks
    private LectureReviewService lectureReviewService;

    @Mock
    private LectureReviewRepository lectureReviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private LectureService lectureService;

    @Mock
    private PurchaseService purchaseService;


    private AuthUser authUser;
    private User user;
    private SaveLectureReviewRequest saveLectureReviewRequest;
    private SaveLectureRequest saveLectureRequest;
    private Lecture lecture;
    private LectureReview lectureReview;

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

        Field lectureField = Lecture.class.getDeclaredField("approval");
        lectureField.setAccessible(true);
        lectureField.set(lecture, Approval.APPROVED);

        //강의 후기
        Constructor<SaveLectureReviewRequest> lectureReviewConstructor = SaveLectureReviewRequest.class.getDeclaredConstructor(
                String.class, Integer.class
        );
        lectureReviewConstructor.setAccessible(true);
        saveLectureReviewRequest = lectureReviewConstructor.newInstance(
                "좋아요",
                5
        );
        lectureReview = LectureReview.from(saveLectureReviewRequest, user, lecture);
    }

    @Test
    void 강의_후기_등록_성공() {
        //given
        Long lectureId = 1L;
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(purchaseService.exitsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);
        given(lectureService.findById(anyLong())).willReturn(lecture);

        //when
        String result = lectureReviewService.saveLectureReview(authUser, lectureId, saveLectureReviewRequest);

        //then
        Assertions.assertNotNull(result);
        verify(lectureReviewRepository, times(1)).save(any(LectureReview.class));
    }

    @Test
    void 강의_후기_수정_성공() {
        //given
        Long lectureId = 1L;
        Long reviewId = 1L;
        user.setId(1L);
        given(lectureService.findById(anyLong())).willReturn(lecture);
        given(lectureReviewRepository.findById(anyLong())).willReturn(Optional.ofNullable(lectureReview));

        //when
        String result = lectureReviewService.updateLectureReview(authUser, lectureId, reviewId, saveLectureReviewRequest);

        //then
        Assertions.assertNotNull(result);
    }

    @Test
    void 강의_후기_다건_조회_성공() {
        //given
        Long lectureId = 1L;
        int page = 1;
        int size = 10;
        PageRequest pageable = PageRequest.of(page - 1, size);

        given(lectureService.findById(anyLong())).willReturn(lecture);
        List<LectureReview> lectureReviewList = List.of(lectureReview);
        Page<LectureReview> lectureReviews = new PageImpl<>(List.of(lectureReview), pageable, lectureReviewList.size());
        given(lectureReviewRepository.findByLectureId(lecture.getId(), pageable)).willReturn(lectureReviews);

        //when
        Page<GetLectureReviewResponse> lectureReviewResponses = lectureReviewService.getLectureReviewList(lectureId, page, size);

        //then
        Assertions.assertNotNull(lectureReviewResponses);
        Assertions.assertEquals(lectureReview.getReview(), lectureReviewResponses.getContent().get(0).getReview());
        Assertions.assertEquals(lectureReview.getReview(), lectureReviewResponses.getContent().get(0).getReview());
        Assertions.assertEquals(lectureReview.getUser().getUsername(), lectureReviewResponses.getContent().get(0).getUserName());
    }

    @Test
    void 강의_후기_삭제_성공() {
        //given
        Long lectureId = 1L;
        Long reviewId = 1L;
        given(lectureService.findById(anyLong())).willReturn(lecture);
        given(lectureReviewRepository.findById(any())).willReturn(Optional.ofNullable(lectureReview));

        //when
        lectureReviewService.deleteLectureReview(authUser, lectureId, reviewId);

        //then
        verify(lectureReviewRepository, times(1)).delete(lectureReview);
    }

}