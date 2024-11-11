package com.devloop.lecture.service;

import com.devloop.attachment.cloudfront.CloudFrontService;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.common.validator.FileValidator;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.entity.LectureVideo;
import com.devloop.lecture.enums.VideoStatus;
import com.devloop.lecture.repository.LectureVideoRepository;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.response.GetLectureVideoDetailResponse;
import com.devloop.lecture.response.GetLectureVideoListResponse;
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
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureVideoServiceTest {
    @InjectMocks
    private LectureVideoService lectureVideoService;

    @Mock
    private UserService userService;

    @Mock
    private LectureService lectureService;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private S3Client s3Client;

    @Mock
    private LectureVideoRepository lectureVideoRepository;

    @Mock
    private PurchaseService purchaseService;

    @Mock
    private CloudFrontService cloudFrontService;

    @Mock
    private S3Service s3Service;

    private AuthUser authUser;
    private User user;
    private SaveLectureRequest saveLectureRequest;
    private Lecture lecture;
    private LectureVideo lectureVideo;

    @BeforeEach
    void setUp() throws Exception {
        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_TUTOR);
        user = User.of("홍길동", "Abc123!", "test@email.com", UserRole.ROLE_TUTOR);

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

        lectureVideo = LectureVideo.of(
                "video.mp4",
                "영상 제목",
                VideoStatus.COMPLETED,
                FileFormat.MP4,
                lecture
        );
        Field lectureField = Lecture.class.getDeclaredField("approval");
        lectureField.setAccessible(true);
        lectureField.set(lecture, Approval.APPROVED);
    }

    @Test
    void 영상_등록_성공() throws IOException {
        //given
        user.setId(1L);
        Long lectureId = 1L;
        MultipartFile multipartFile = mock(MultipartFile.class);
        String title = "테스트 영상";
        given(userService.findByUserId(any())).willReturn(user);
        given(lectureService.findById(any())).willReturn(lecture);
        given(multipartFile.getContentType()).willReturn("video/mp4");
        given(fileValidator.mapStringToFileFormat(any())).willReturn(FileFormat.MP4);
        given(multipartFile.getSize()).willReturn(5L * 1024 * 1024);
        given(multipartFile.getInputStream()).willReturn(mock(InputStream.class));
        given(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class))).willReturn(CreateMultipartUploadResponse.builder().uploadId("uploadId").build());

        UploadPartResponse uploadPartResponse = UploadPartResponse.builder().eTag("sampleETag").build();
        given(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class))).willReturn(uploadPartResponse);

        CompleteMultipartUploadResponse completeMultipartUploadResponse = CompleteMultipartUploadResponse.builder().build();
        given(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class))).willReturn(completeMultipartUploadResponse);
        LectureVideo lectureVideo = LectureVideo.of(
                "video.mp4",
                "영상 제목",
                VideoStatus.COMPLETED,
                FileFormat.MP4,
                lecture
        );
        given(lectureVideoRepository.save(any())).willReturn(lectureVideo);

        //when
        String result = lectureVideoService.uploadLectureVideo(authUser, lectureId, multipartFile, title);

        //then
        Assertions.assertNotNull(result);
        verify(lectureVideoRepository, times(1)).save(any(LectureVideo.class));
    }

    @Test
    void 영상_다건_조회_성공() {
        //given
        Long lectureId = 1L;
        given(lectureService.findById(any())).willReturn(lecture);

        List<LectureVideo> lectureVideos = List.of(lectureVideo);
        given(lectureVideoRepository.findAllByLectureId(any())).willReturn(lectureVideos);

        //when
        List<GetLectureVideoListResponse> lectureVideoListResponseList = lectureVideoService.getLectureVideoList(lectureId);

        //then
        Assertions.assertNotNull(lectureVideoListResponseList);
        Assertions.assertEquals(lectureVideoListResponseList.get(0).getTitle(), "영상 제목");
    }

    @Test
    void 수강유저_영상_단건_조회_성공() throws Exception {
        //given
        Long lectureId = 1L;
        Long videoId = 1L;
        String signedURL = "signedURL";
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(purchaseService.exitsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);
        given(lectureService.findById(anyLong())).willReturn(lecture);
        given(lectureVideoRepository.findById(anyLong())).willReturn(Optional.ofNullable(lectureVideo));
        given(cloudFrontService.generateSignedUrl(anyString(), anyLong())).willReturn(signedURL);

        //when
        GetLectureVideoDetailResponse lectureVideoDetailResponse = lectureVideoService.getLectureVideo(authUser, lectureId, videoId);

        //then
        Assertions.assertNotNull(lectureVideoDetailResponse);
        Assertions.assertEquals(lectureVideoDetailResponse.getTitle(), lectureVideo.getTitle());
        Assertions.assertEquals(lectureVideoDetailResponse.getVideoURL(), signedURL);
    }

    @Test
    void 수강유저_또는_관리자가_아닌_경우_단건_조회_실패() {
        //given
        Long lectureId = 1L;
        Long videoId = 1L;

        given(userService.findByUserId(anyLong())).willReturn(user);
        given(purchaseService.exitsByUserIdAndProductId(anyLong(), anyLong())).willReturn(false);

        //when & then
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> lectureVideoService.getLectureVideo(authUser, lectureId, videoId));

        Assertions.assertEquals(ErrorStatus._ACCESS_PERMISSION_DENIED, exception.getErrorCode());
    }

    @Test
    void SignedURL_생성_실패() throws Exception {
        //given
        Long lectureId = 1L;
        Long videoId = 1L;
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(purchaseService.exitsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);
        given(lectureService.findById(anyLong())).willReturn(lecture);
        given(lectureVideoRepository.findById(anyLong())).willReturn(Optional.ofNullable(lectureVideo));
        given(cloudFrontService.generateSignedUrl(anyString(), anyLong())).willThrow(new Exception());

        //when & then
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> lectureVideoService.getLectureVideo(authUser, lectureId, videoId));

        Assertions.assertEquals(exception.getErrorCode(), ErrorStatus._INVALID_URL_FORMAT);
    }

    @Test
    void 영상_삭제_성공() {
        //given
        Long lectureId = 1L;
        Long videoId = 1L;
        user.setId(1L);
        given(lectureService.findById(anyLong())).willReturn(lecture);
        given(lectureVideoRepository.findById(anyLong())).willReturn(Optional.ofNullable(lectureVideo));
        //when
        lectureVideoService.deleteVideo(authUser, lectureId, videoId);

        //then
        verify(lectureVideoRepository, times(1)).delete(lectureVideo);
        verify(s3Service, times(1)).deleteVideo(lectureVideo.getFileName());
    }
}