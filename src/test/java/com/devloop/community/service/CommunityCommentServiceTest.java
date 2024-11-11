package com.devloop.community.service;

import com.devloop.common.AuthUser;
import com.devloop.common.enums.Category;
import com.devloop.common.utils.NotificationHandler;
import com.devloop.community.entity.Community;
import com.devloop.communitycomment.entity.CommunityComment;
import com.devloop.communitycomment.repository.CommunityCommentRepository;
import com.devloop.communitycomment.request.CommentSaveRequest;
import com.devloop.communitycomment.request.CommentUpdateRequest;
import com.devloop.communitycomment.response.CommentSaveResponse;
import com.devloop.communitycomment.response.CommentUpdateResponse;
import com.devloop.communitycomment.service.CommunityCommentService;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityCommentServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private CommunityService communityService;
    @Mock
    private NotificationHandler notificationHandler;
    @Mock
    private CommunityCommentRepository communityCommentRepository;

    @InjectMocks
    private CommunityCommentService communityCommentService;

    private User user;
    private User postAuthor;
    private AuthUser authUser;
    private Community community;
    private CommunityComment communityComment;


    @BeforeEach
    public void setUp() throws Exception {
        user = User.of("댓글작성자", "a@example.com", "password123", UserRole.ROLE_USER);
        postAuthor = User.of("게시글작성자", "a@example.com", "password123", UserRole.ROLE_USER);
        authUser = new AuthUser(user.getId(), user.getEmail(), UserRole.ROLE_USER);

        // 리플렉션을 사용하여 User ID 설정
        Field userIdField = User.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(user, 1L); // 댓글 작성자 ID 설정
        userIdField.set(postAuthor, 2L); // 게시글 작성자 ID 설정

        authUser = new AuthUser(user.getId(), user.getEmail(), UserRole.ROLE_USER);

        community = Community.of(
                "Community Title",
                "Community Content",
                Category.APP_DEV,
                postAuthor);

        // 리플렉션을 통해 ID 수동 설정
        Field communityIdField = Community.class.getDeclaredField("id");
        communityIdField.setAccessible(true);
        communityIdField.set(community, 1L); // Community ID 설정

        // 댓글 객체 초기화
        communityComment = CommunityComment.of(
                "댓글 내용",
                community,
                user);

        Field commentIdField = CommunityComment.class.getDeclaredField("id");
        commentIdField.setAccessible(true);
        commentIdField.set(communityComment, 1L); // CommunityComment ID 설정

    }


    @Test
    void 댓글_작성_성공() throws Exception {
        // 리플렉션을 사용하여 CommentSaveRequest 객체 생성
        Constructor<CommentSaveRequest> constructor = CommentSaveRequest.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true); // 접근 제어 해제
        CommentSaveRequest commentSaveRequest = constructor.newInstance("댓글 내용");

        when(communityService.getCommunityId(community.getId())).thenReturn(community);
        when(userService.findByUserId(authUser.getId())).thenReturn(user);
        when(communityCommentRepository.save(any(CommunityComment.class))).thenReturn(communityComment);

        // when
        CommentSaveResponse response = communityCommentService.createComment(authUser, commentSaveRequest, community.getId());

        // then
        assertEquals(communityComment.getId(), response.getCommentId());
        assertEquals(communityComment.getContent(), response.getContent());
        verify(communityCommentRepository, Mockito.times(1)).save(any(CommunityComment.class));
    }

    @Test
    void 댓글_수정_성공() throws Exception {

        // CommentUpdateRequest 객체를 리플렉션을 사용하여 생성
        Constructor<CommentUpdateRequest> constructor = CommentUpdateRequest.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true); // 접근 제어 해제
        CommentUpdateRequest commentUpdateRequest = constructor.newInstance("댓글수정");

        when(communityCommentRepository.findById(communityComment.getId())).thenReturn(Optional.of(communityComment));

        // communityCommentRepository.save()가 communityComment 객체를 반환하도록 설정
        when(communityCommentRepository.save(any(CommunityComment.class))).thenReturn(communityComment);

        // 댓글 수정 메서드 호출
        CommentUpdateResponse response = communityCommentService.updateComment(authUser, commentUpdateRequest, community.getId(), communityComment.getId());

        // 결과 검증
        assertEquals("댓글수정", response.getContent()); // 응답의 내용이 수정된 내용과 일치하는지 확인
        verify(communityCommentRepository, Mockito.times(1)).save(any(CommunityComment.class));
    }

    @Test
    void 댓글_삭제_성공() throws Exception {
        // given
        when(communityCommentRepository.findById(communityComment.getId())).thenReturn(Optional.of(communityComment));

        // when
        communityCommentService.deleteComment(authUser, community.getId(), communityComment.getId());

        // then
        verify(communityCommentRepository, Mockito.times(1)).delete(communityComment);
    }
}

