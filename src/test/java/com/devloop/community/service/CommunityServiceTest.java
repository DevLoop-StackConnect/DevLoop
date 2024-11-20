//package com.devloop.community.service;
//
//
//import com.devloop.attachment.entity.CommunityAttachment;
//import com.devloop.attachment.enums.FileFormat;
//import com.devloop.attachment.s3.S3Service;
//import com.devloop.attachment.service.CommunityAttachmentService;
//import com.devloop.common.AuthUser;
//import com.devloop.common.apipayload.dto.CommunitySimpleResponseDto;
//import com.devloop.common.apipayload.status.ErrorStatus;
//import com.devloop.common.enums.Category;
//import com.devloop.common.exception.ApiException;
//import com.devloop.community.entity.Community;
//import com.devloop.community.entity.ResolveStatus;
//import com.devloop.community.event.CommunityCreatedEvent;
//import com.devloop.community.event.CommunityUpdatedEvent;
//import com.devloop.community.repository.jpa.CommunityRepository;
//import com.devloop.community.request.CommunitySaveRequest;
//import com.devloop.community.request.CommunityUpdateRequest;
//import com.devloop.community.response.CommunityDetailResponse;
//import com.devloop.community.response.CommunitySaveResponse;
//import com.devloop.community.response.CommunitySimpleResponse;
//import com.devloop.user.entity.User;
//import com.devloop.user.enums.UserRole;
//import com.devloop.user.service.UserService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.lang.reflect.Constructor;
//import java.net.URL;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//
//@ExtendWith(MockitoExtension.class)
//class CommunityServiceTest {
//
//    @Mock
//    private CommunityRepository communityRepository;
//    @Mock
//    private UserService userService;
//    @Mock
//    private S3Service s3Service;
//    @Mock
//    private CommunityAttachmentService communityAttachmentService;
//
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    @InjectMocks
//    private CommunityService communityService;
//
//    private Community community;
//    private User user;
//    private AuthUser authUser;
//    private CommunitySaveRequest communitySaveRequest;
//    private MultipartFile file;
//
//
//    @BeforeEach
//    public void setUp() throws Exception {
//
//        user = User.of("게시글 작성자", "user@example.com", "password123", UserRole.ROLE_USER);
//        user.setId(1L);
//        authUser = new AuthUser(user.getId(), user.getEmail(), UserRole.ROLE_USER);
//        community = Community.of(
//                "제목",
//                "내용",
//                Category.APP_DEV,
//                user
//        );
//
//
//        // CommunitySaveRequest 객체 생성 (리플렉션 사용)
//        Constructor<CommunitySaveRequest> constructor = CommunitySaveRequest.class.getDeclaredConstructor(
//                String.class, String.class, Category.class // title,content,category
//        );
//        constructor.setAccessible(true); //접근가능하게.
//        communitySaveRequest = constructor.newInstance("제목","내용",Category.APP_DEV);
//        CommunitySaveRequest communitySaveRequest = constructor.newInstance(
//                "제목",
//                "내용",
//                Category.APP_DEV
//        );
//
//        // Mock MultipartFile 설정 (첨부파일 테스트에 사용)
//        file = Mockito.mock(MultipartFile.class);
//
//    }
//
//    @Test
//    void 커뮤니티글_작성_성공_파일없음()  {
//
//        // given
//        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);
//        Mockito.when(communityRepository.save(any(Community.class))).thenReturn(community);
//
//        // when
//        CommunitySaveResponse response = communityService.createCommunity(authUser, null, communitySaveRequest);
//
//
//        // then
//        Assertions.assertEquals(community.getId(), response.getCommunityId());
//        Assertions.assertEquals(community.getTitle(), response.getTitle());
//        Mockito.verify(communityRepository, Mockito.times(1)).save(any(Community.class));
//        Mockito.verify(s3Service, Mockito.times(0)).uploadFile(any(MultipartFile.class), any(User.class), any(Community.class));
//        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(CommunityCreatedEvent.class));
//    }
//
//    @Test
//    public void 커뮤니티글_게시_성공_파일있음()  {
//
//        // given
//        Mockito.when(userService.findByUserId(authUser.getId())).thenReturn(user);
//        Mockito.when(communityRepository.save(any(Community.class))).thenReturn(community);
//        Mockito.when(file.isEmpty()).thenReturn(false); // 파일이 있는 상태
//
//        // when
//        CommunitySaveResponse response = communityService.createCommunity(authUser, file, communitySaveRequest);
//
//        // then
//        Assertions.assertEquals(community.getId(), response.getCommunityId());
//        Assertions.assertEquals(community.getTitle(), response.getTitle());
//        Mockito.verify(communityRepository, Mockito.times(1)).save(any(Community.class));
//        Mockito.verify(s3Service, Mockito.times(1)).uploadFile(eq(file), eq(user), any(Community.class));
//        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(CommunityCreatedEvent.class));
//    }
//
//    @Test
//    public void 커뮤니티글_다건조회_성공(){
//        // given
//        int page = 1;
//        int size = 5;
//        Pageable pageable = PageRequest.of(page - 1, size);
//
//        List<CommunitySimpleResponseDto> communityList = List.of(
//                CommunitySimpleResponseDto.builder()
//                        .communityId(1L)
//                        .title("제목1")
//                        .status(ResolveStatus.UNSOLVED)
//                        .category(Category.APP_DEV)
//                        .build(),
//                CommunitySimpleResponseDto.builder()
//                        .communityId(2L)
//                        .title("제목2")
//                        .status(ResolveStatus.SOLVED)
//                        .category(Category.GAME_DEV)
//                        .build()
//        );
//        Page<CommunitySimpleResponseDto> communityPage = new PageImpl<>(communityList, pageable, communityList.size());
//
//        Mockito.when(communityRepository.findAllSimple(pageable)).thenReturn(communityPage);
//
//        // when
//        Page<CommunitySimpleResponse> responsePage = communityService.getCommunities(page, size);
//
//        // then
//        Assertions.assertEquals(communityList.size(), responsePage.getTotalElements());
//        Assertions.assertEquals("제목1", responsePage.getContent().get(0).getTitle());
//        Assertions.assertEquals("제목2", responsePage.getContent().get(1).getTitle());
//        Mockito.verify(communityRepository, Mockito.times(1)).findAllSimple(pageable);
//    }
//
//    @Test
//    public void 커뮤니티글_단건조회_성공() throws Exception {
//        // given
//        Long communityId = 1L;
//        String imageUrl = "https://example.com/image.jpg";
//        URL url = new URL(imageUrl);
//        FileFormat fileFormat = FileFormat.JPG;
//        String fileName = "test_image.jpg";
//
//        // CommunityAttachment 리플렉션 사용
//        Constructor<CommunityAttachment> constructor = CommunityAttachment.class.getDeclaredConstructor(
//                Long.class, URL.class, FileFormat.class, String.class
//        );
//        constructor.setAccessible(true); // 접근 제어 풀기
//        CommunityAttachment communityAttachment = constructor.newInstance(communityId, url, fileFormat, fileName);
//
//        // Community 및 CommunityAttachment 설정
//        Mockito.when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
//        Mockito.when(communityAttachmentService.getCommunityAttachment(communityId))
//                .thenReturn(Optional.of(communityAttachment));
//
//        // when
//        CommunityDetailResponse response = communityService.getCommunity(communityId);
//
//        // then
//        Assertions.assertEquals(community.getId(), response.getCommunityId());
//        Assertions.assertEquals(community.getTitle(), response.getTitle());
//        Assertions.assertEquals(imageUrl, response.getImageUrl());
//        Mockito.verify(communityRepository, Mockito.times(1)).findById(communityId);
//        Mockito.verify(communityAttachmentService, Mockito.times(1)).getCommunityAttachment(communityId);
//    }
//
//    @Test
//    public void 수정_성공() throws Exception {
//        // given
//        Long communityId = 1L;
//
//        // community 객체 초기화
//        community = Community.of("제목", "내용", Category.APP_DEV, user);
//
//        // CommunityUpdateRequest 객체 생성 (리플렉션 사용)
//        Constructor<CommunityUpdateRequest> constructor = CommunityUpdateRequest.class.getDeclaredConstructor(
//                String.class, String.class, ResolveStatus.class, Category.class
//        );
//        constructor.setAccessible(true);
//        CommunityUpdateRequest communityUpdateRequest = constructor.newInstance(
//                "수정된 제목", "수정된 내용", ResolveStatus.SOLVED, Category.APP_DEV
//        );
//
//        // Mock 설정: community 객체와 작성자가 동일함
//        Mockito.when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
//
//        // Mock 파일 설정 (file이 비어 있지 않은 경우)
//        Mockito.when(file.isEmpty()).thenReturn(false);
//
//        // when
//        CommunityDetailResponse response = communityService.updateCommunity(authUser, communityId, communityUpdateRequest, file);
//
//        // then
//        Assertions.assertEquals("수정된 제목", response.getTitle());
//        Assertions.assertEquals("수정된 내용", response.getContent());
//        Assertions.assertEquals(ResolveStatus.SOLVED.getDescription(), response.getStatus());
//        Assertions.assertEquals(Category.APP_DEV.getDescription(), response.getCategory());
//
//        // Mock 호출 검증
//        Mockito.verify(communityRepository, Mockito.times(1)).findById(communityId);
//        Mockito.verify(communityRepository, Mockito.times(1)).save(any(Community.class));
//        Mockito.verify(s3Service, Mockito.times(1)).uploadFile(file, user, community);
//        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(any(CommunityUpdatedEvent.class)); // 추가
//
//    }
//    @Test
//    public void 다른사람글_삭제_성공() {
//        // given
//        Long communityId = 1L;
//
//        // 게시글 작성자 및 AuthUser 객체 설정
//        Mockito.when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
//
//        // when
//        communityService.deleteCommunity(authUser, communityId);
//
//        // then
//        // communityRepository.delete 메서드가 community 객체와 함께 호출되었는지 검증
//        Mockito.verify(communityRepository, Mockito.times(1)).delete(community);
//    }
//
//    @Test
//    public void 게시글_삭제_실패_권한없음() {
//        // given
//        Long communityId = 1L;
//
//        // 다른 사용자 생성 (게시글 작성자가 아닌 사용자)
//        User otherUser = User.of("다른 사용자", "other@example.com", "password123", UserRole.ROLE_USER);
//        AuthUser authOtherUser = new AuthUser(otherUser.getId(), otherUser.getEmail(), UserRole.ROLE_USER);
//
//        Mockito.when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
//
//        // when & then
//        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
//            communityService.deleteCommunity(authOtherUser, communityId);
//        });
//
//        // 예외 메시지 검증
//        assertEquals(ErrorStatus._PERMISSION_DENIED, exception.getErrorCode());
//        Mockito.verify(communityRepository, Mockito.times(0)).delete(any(Community.class));
//    }
//
//}
//
