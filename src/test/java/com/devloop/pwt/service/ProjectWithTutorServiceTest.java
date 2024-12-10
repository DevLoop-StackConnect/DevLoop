package com.devloop.pwt.service;

import com.devloop.attachment.entity.PWTAttachment;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.s3.S3Service;
import com.devloop.attachment.service.PWTAttachmentService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto;
import com.devloop.common.enums.Approval;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.party.event.PartyCreatedEvent;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.pwt.event.PwtCreatedEvent;
import com.devloop.pwt.event.PwtDeletedEvent;
import com.devloop.pwt.event.PwtUpdatedEvent;
import com.devloop.pwt.repository.jpa.ProjectWithTutorRepository;
import com.devloop.pwt.request.ProjectWithTutorSaveRequest;
import com.devloop.pwt.request.ProjectWithTutorUpdateRequest;
import com.devloop.pwt.response.ProjectWithTutorDetailResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectWithTutorServiceTest {
    @Mock
    private ProjectWithTutorRepository projectWithTutorRepository;

    @Mock
    private UserService userService;

    @Mock
    private S3Service s3Service;

    @Mock
    private PWTAttachmentService pwtAttachmentService;

    @InjectMocks
    private ProjectWithTutorService projectWithTutorService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AuthUser authUser;
    private MockMultipartFile mockFile;
    private Object instance;
    private PWTAttachment pwtAttachment;
    private URL url;

    @BeforeEach
    void setUp() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, MalformedURLException {
        // given
        mockFile = new MockMultipartFile(
                "file",
                "testfile1.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        Class<?> request = Class.forName("com.devloop.pwt.request.ProjectWithTutorSaveRequest");
        Constructor<?> constructor = request.getDeclaredConstructor(
                String.class,
                String.class,
                BigDecimal.class,
                LocalDateTime.class,
                Integer.class,
                Level.class,
                Category.class);
        constructor.setAccessible(true);
        instance = constructor.newInstance(
                "Project Title",
                "Project Description",
                BigDecimal.valueOf(10000),
                LocalDateTime.now().plusDays(10),
                10,
                Level.EASY,
                Category.APP_DEV);
        url = new URL("https://example.com");
    }
    @Nested
    class saveProjectWithTutorTest{
        @Test
        void ROLE_USER일때_예외처리(){
            //given
            authUser = new AuthUser(1L, "skawlsgus@naver.com", UserRole.ROLE_USER);
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
            given(userService.findByUserId(anyLong())).willReturn(user);
            //When,Then
            assertThrows(ApiException.class,()->projectWithTutorService.saveProjectWithTutor(authUser,mockFile,(ProjectWithTutorSaveRequest) instance));
        }

        @Test
        void ROLE_TUTOR일때_게시글작성_성공(){
            //given
            authUser = new AuthUser(1L, "skawlsgus@naver.com", UserRole.ROLE_TUTOR);
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_TUTOR);
            given(userService.findByUserId(anyLong())).willReturn(user);

            //when,then
            assertEquals(String.format("%s 님의 튜터랑 함께하는 협업 프로젝트 게시글이 작성 완료되었습니다. 승인까지 3~5일 정도 소요될 수 있습니다.", user.getUsername()),
                    projectWithTutorService.saveProjectWithTutor(authUser,mockFile,(ProjectWithTutorSaveRequest) instance));
            verify(projectWithTutorRepository, times(1)).save(any(ProjectWithTutor.class));
            verify(s3Service, times(1)).uploadFile(any(),any(),any());
            Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(PwtCreatedEvent.class));
        }
    }
//    @Nested
//    class getProjectWithTutorTest{
////        @Test
////        void 승인안된PWT_조회_시_예외처리(){
////            //given
////            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
////            ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
////                    "Project Title",
////                    "Project Description",
////                    BigDecimal.valueOf(10000),
////                    LocalDateTime.now().plusDays(10),
////                    10,
////                    Level.EASY,
////                    Category.APP_DEV,
////                    user);
////            given(projectWithTutorRepository.findById(anyLong())).willReturn(Optional.of(projectWithTutor));
////
////            //when,then
////            assertThrows(ApiException.class,()->projectWithTutorService.getProjectWithTutor(anyLong()));
////        }
//
//        @Test
//        void 승인된PWT_조회성공(){
//            //given
//            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
//            ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
//                    "Project Title",
//                    "Project Description",
//                    BigDecimal.valueOf(10000),
//                    LocalDateTime.now().plusDays(10),
//                    10,
//                    Level.EASY,
//                    Category.APP_DEV,
//                    user);
//            ReflectionTestUtils.setField(projectWithTutor,"approval", Approval.APPROVED);
//            pwtAttachment = PWTAttachment.of(1L, url, FileFormat.JPG, "파일 이름");
//            given(projectWithTutorRepository.findById(anyLong())).willReturn(Optional.of(projectWithTutor));
//            given(pwtAttachmentService.findPwtAttachmentByPwtId(any())).willReturn(pwtAttachment);
//            //when
//            ProjectWithTutorDetailResponse projectWithTutorDetailResponse = projectWithTutorService.getProjectWithTutor(1L);
//            //then
//            assertEquals(projectWithTutor.getTitle(),projectWithTutorDetailResponse.getTitle());
//            assertEquals(projectWithTutor.getDescription(),projectWithTutorDetailResponse.getDescription());
//            assertEquals(projectWithTutor.getPrice(),projectWithTutorDetailResponse.getPrice());
//            assertEquals("모집중",projectWithTutorDetailResponse.getStatus().toString());
//            assertEquals(projectWithTutor.getDeadline(),projectWithTutorDetailResponse.getDeadline());
//            assertEquals(projectWithTutor.getMaxParticipants(),projectWithTutorDetailResponse.getMaxParticipants());
//            assertEquals("쉬움",projectWithTutorDetailResponse.getLevel().toString());
//            assertEquals(user.getUsername(),projectWithTutorDetailResponse.getTutorName());
//            assertEquals(pwtAttachment.getImageURL().toString(),projectWithTutorDetailResponse.getAttachmentUrl().toString());
//        }
//    }
    @Nested
    class getAllProjectWithTutorsTest{
        @Test
        void 승인된_PWT_없을_때_예외처리(){
            Page<ProjectWithTutorResponseDto> projectWithTutors =Page.empty();
            given(projectWithTutorRepository.findAllApprovedProjectWithTutor(any(),any())).willReturn(projectWithTutors);
            //when,then
            assertThrows(ApiException.class,()->projectWithTutorService.getAllProjectWithTutors(1,10));
        }

        @Test
        void 다건조회_성공() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
            Class<?> request = Class.forName("com.devloop.common.apipayload.dto.ProjectWithTutorResponseDto");
            Constructor<?> responseConstructor = request.getDeclaredConstructor(
                Long.class,
                String.class,
                BigDecimal.class,
                ProjectWithTutorStatus.class,
                LocalDateTime.class,
                Integer.class,
                Level.class,
                User.class);
            responseConstructor.setAccessible(true);
            instance = responseConstructor.newInstance(
                    1L,
                    "예시",
                    BigDecimal.valueOf(1000),
                    ProjectWithTutorStatus.IN_PROGRESS,
                    LocalDateTime.now().plusDays(10),
                    1,
                    Level.EASY,
                    user
                    );
            Page<ProjectWithTutorResponseDto> projectWithTutors = new PageImpl<>(List.of((ProjectWithTutorResponseDto)instance));
            given(projectWithTutorRepository.findAllApprovedProjectWithTutor(any(),any())).willReturn(projectWithTutors);

            //when
            projectWithTutorService.getAllProjectWithTutors(1,10);

            //then
            assertEquals(1, projectWithTutors.getTotalElements());
        }
    }
    @Nested
    class updateProjectWithTutor{
        @Test
        void PWT게시글_작성자가_아닐때_예외처리() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            //given
            authUser = new AuthUser(1L, "skawlsgus@naver.com", UserRole.ROLE_USER);
            Long projectId = 1L;
            mockFile = new MockMultipartFile(
                    "file",
                    "testfile.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()
            );
            Class<?> request = Class.forName("com.devloop.pwt.request.ProjectWithTutorUpdateRequest");
            Constructor<?> responseConstructor = request.getDeclaredConstructor(
                    String.class,
                    String.class,
                    BigDecimal.class,
                    LocalDateTime.class,
                    Integer.class,
                    Level.class,
                    Category.class);
            responseConstructor.setAccessible(true);
            instance = responseConstructor.newInstance(
                    "제목",
                    "설명",
                    BigDecimal.valueOf(1000),
                    LocalDateTime.now().plusDays(10),
                    5,
                    Level.EASY,
                    Category.APP_DEV
            );
            ProjectWithTutorUpdateRequest projectWithTutorUpdateRequest = (ProjectWithTutorUpdateRequest) instance;
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
            User user2 = User.of("홍길동", "ghdrlfehd@naver.com", "123!!", UserRole.ROLE_USER);
            ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
                    "Project Title",
                    "Project Description",
                    BigDecimal.valueOf(10000),
                    LocalDateTime.now().plusDays(10),
                    10,
                    Level.EASY,
                    Category.APP_DEV,
                    user);
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user2, "id", 2L);
            ReflectionTestUtils.setField(projectWithTutor, "user", user2);
            given(userService.findByUserId(anyLong())).willReturn(user);
            given((projectWithTutorRepository.findById(anyLong()))).willReturn(Optional.of(projectWithTutor));

            //when,then
            assertThrows(ApiException.class,()->projectWithTutorService.updateProjectWithTutor(authUser,projectId,mockFile,projectWithTutorUpdateRequest));
        }
        @Test
        void PWT게시글_수정_성공() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            //given
            authUser = new AuthUser(1L, "skawlsgus@naver.com", UserRole.ROLE_USER);
            Long projectId = 1L;
            mockFile = new MockMultipartFile(
                    "file",
                    "testfile.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()
            );
            Class<?> request = Class.forName("com.devloop.pwt.request.ProjectWithTutorUpdateRequest");
            Constructor<?> responseConstructor = request.getDeclaredConstructor(
                    String.class,
                    String.class,
                    BigDecimal.class,
                    LocalDateTime.class,
                    Integer.class,
                    Level.class,
                    Category.class);
            responseConstructor.setAccessible(true);
            instance = responseConstructor.newInstance(
                    "제목",
                    "설명",
                    BigDecimal.valueOf(1000),
                    LocalDateTime.now().plusDays(10),
                    5,
                    Level.EASY,
                    Category.APP_DEV
            );
            ProjectWithTutorUpdateRequest projectWithTutorUpdateRequest = (ProjectWithTutorUpdateRequest) instance;
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
            ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
                    "Project Title",
                    "Project Description",
                    BigDecimal.valueOf(10000),
                    LocalDateTime.now().plusDays(10),
                    10,
                    Level.EASY,
                    Category.APP_DEV,
                    user);
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(projectWithTutor, "user", user);
            ReflectionTestUtils.setField(projectWithTutor, "id", 1L);
            given(userService.findByUserId(anyLong())).willReturn(user);
            given((projectWithTutorRepository.findById(anyLong()))).willReturn(Optional.of(projectWithTutor));
            given(pwtAttachmentService.findPwtAttachmentByPwtId(anyLong())).willReturn((pwtAttachment));

            //when
            String response = projectWithTutorService.updateProjectWithTutor(authUser,projectId,mockFile,projectWithTutorUpdateRequest);

            //then
            assertEquals(String.format("%s 게시글이 수정되었습니다.", projectWithTutor.getTitle()), response);
            Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(PwtUpdatedEvent.class));

        }
    }
    @Nested
    class deleteProjectWithTutor{
        @Test
        void ROLE_USER인_유저가_PWT_삭제요청하면_예외처리(){
            //given
            authUser = new AuthUser(1L, "skawlsgus@naver.com", UserRole.ROLE_USER);
            Long projectId = 1L;
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_USER);
            User user2 = User.of("홍길동", "ghdrlfehd@naver.com", "123!!", UserRole.ROLE_TUTOR);
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user2, "id", 2L);
            given(userService.findByUserId(anyLong())).willReturn(user);
            ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
                    "Project Title",
                    "Project Description",
                    BigDecimal.valueOf(10000),
                    LocalDateTime.now().plusDays(10),
                    10,
                    Level.EASY,
                    Category.APP_DEV,
                    user);
            ReflectionTestUtils.setField(projectWithTutor, "user", user2);
            given((projectWithTutorRepository.findById(anyLong()))).willReturn(Optional.of(projectWithTutor));

            //when,then
            assertThrows(ApiException.class,()->projectWithTutorService.deleteProjectWithTutor(authUser,projectId));
        }
        @Test
        void ROLE_ADMIN인_유저가_PWT_삭제요청하면_성공(){
            //given
            authUser = new AuthUser(1L, "skawlsgus@naver.com", UserRole.ROLE_ADMIN);
            Long projectId = 1L;
            User user = User.of("남진현", "skawlsgus@naver.com", "123!!", UserRole.ROLE_ADMIN);
            ReflectionTestUtils.setField(user, "id", 1L);
            given(userService.findByUserId(anyLong())).willReturn(user);
            ProjectWithTutor projectWithTutor = ProjectWithTutor.of(
                    "Project Title",
                    "Project Description",
                    BigDecimal.valueOf(10000),
                    LocalDateTime.now().plusDays(10),
                    10,
                    Level.EASY,
                    Category.APP_DEV,
                    user);
            ReflectionTestUtils.setField(projectWithTutor, "user", user);
            ReflectionTestUtils.setField(projectWithTutor, "approval", Approval.WAITE);
            ReflectionTestUtils.setField(projectWithTutor, "id", 1L);
            pwtAttachment = PWTAttachment.of(1L, url, FileFormat.JPG, "파일 이름");
            given(projectWithTutorRepository.findById(anyLong())).willReturn(Optional.of(projectWithTutor));
            given(pwtAttachmentService.findPwtAttachmentByPwtId(anyLong())).willReturn(pwtAttachment);
            doNothing().when(s3Service).delete(any());

            //when
            projectWithTutorService.deleteProjectWithTutor(authUser,projectId);

            //then
            verify(projectWithTutorRepository, times(1)).delete((ProjectWithTutor) any());
            Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(PwtDeletedEvent.class));
        }
        /*@Test
        void 테스트코드_실패방지_테스트(){}*/
    }
}