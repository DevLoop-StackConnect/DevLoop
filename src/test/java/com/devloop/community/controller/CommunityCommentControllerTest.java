package com.devloop.community.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.utils.JwtUtil;
import com.devloop.communitycomment.controller.CommunityCommentController;
import com.devloop.communitycomment.request.CommentSaveRequest;
import com.devloop.communitycomment.request.CommentUpdateRequest;
import com.devloop.communitycomment.response.CommentResponse;
import com.devloop.communitycomment.response.CommentSaveResponse;
import com.devloop.communitycomment.response.CommentUpdateResponse;
import com.devloop.communitycomment.service.CommunityCommentService;
import com.devloop.config.JwtAuthenticationToken;
import com.devloop.config.WebSecurityConfig;
import com.devloop.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommunityCommentController.class)
@AutoConfigureMockMvc
@Import({WebSecurityConfig.class, JwtUtil.class})
@TestPropertySource(properties = {
        "jwt.secret.key=7Iqk7YyM66W07YOA7L2U65Sp7YG065+9U3ByaW5n6rCV7J2Y7Yqc7YSw7LWc7JuQ67mI7J6F64uI64ukLg==" // JWT 테스트용 키
})
@MockBean(JpaMetamodelMappingContext.class)
public class CommunityCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private JwtAuthenticationToken adminAuthenticationToken;
    private JwtAuthenticationToken userAuthenticationToken;

    @MockBean
    private CommunityCommentService communityCommentService;

    @BeforeEach
    public void setUp() {
        AuthUser adminUser = new AuthUser(1L, "admin@example.com", UserRole.ROLE_ADMIN);
        adminAuthenticationToken = new JwtAuthenticationToken(adminUser);

        AuthUser normalUser = new AuthUser(2L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);
    }

    @Test
    void createComment_성공() throws Exception {
        // Mock 데이터 준비
        CommentSaveResponse mockResponse = CommentSaveResponse.of(1L, "Test Content", LocalDateTime.now());

        Constructor<CommentSaveRequest> constructor = CommentSaveRequest.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true); // protected 접근 허용
        CommentSaveRequest expectedRequest = constructor.newInstance("comment");

        // Mock 설정
        Mockito.when(communityCommentService.createComment(
                        Mockito.any(AuthUser.class),
                        Mockito.any(CommentSaveRequest.class),
                        Mockito.eq(1L)))
                .thenReturn(mockResponse);

        // 요청 및 검증
        mockMvc.perform(post("/api/v1/communities/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Test Content\"}")
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(mockResponse.getCommentId()))
                .andExpect(jsonPath("$.data.content").value(mockResponse.getContent()));

        // Mock 호출 검증
        Mockito.verify(communityCommentService, Mockito.times(1))
                .createComment(Mockito.any(AuthUser.class), Mockito.any(CommentSaveRequest.class), Mockito.eq(1L));
    }

    @Test
    void updateComment_성공() throws Exception {
        // Mock 데이터 준비
        CommentUpdateResponse mockResponse = CommentUpdateResponse.of(1L, "Updated Content", LocalDateTime.now());

        Constructor<CommentUpdateRequest> constructor = CommentUpdateRequest.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true); // protected 접근 허용
        CommentUpdateRequest expectedRequest = constructor.newInstance("comment");


        // Mock 설정
        Mockito.when(communityCommentService.updateComment(
                        Mockito.any(AuthUser.class),
                        Mockito.any(CommentUpdateRequest.class),
                        Mockito.eq(1L),
                        Mockito.eq(1L)))
                .thenReturn(mockResponse);

        // 요청 및 검증
        mockMvc.perform(patch("/api/v1/communities/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Updated Content\"}")
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(mockResponse.getCommentId()))
                .andExpect(jsonPath("$.data.content").value(mockResponse.getContent()));

        // Mock 호출 검증
        Mockito.verify(communityCommentService, Mockito.times(1))
                .updateComment(Mockito.any(AuthUser.class), Mockito.any(CommentUpdateRequest.class), Mockito.eq(1L), Mockito.eq(1L));
    }

    @Test
    void deleteComment_성공() throws Exception {
        // 요청 및 검증
        mockMvc.perform(delete("/api/v1/communities/1/comments/1")
                        .with(authentication(adminAuthenticationToken)))
                .andExpect(status().isNoContent());

        // Mock 호출 검증
        Mockito.verify(communityCommentService, Mockito.times(1))
                .deleteComment(Mockito.any(AuthUser.class), Mockito.eq(1L), Mockito.eq(1L));
    }

    @Test
    void getComments_성공() throws Exception {
        // Mock 데이터 준비
        CommentResponse commentResponse = CommentResponse.of(1L, "Test Content", "user1", LocalDateTime.now());
        List<CommentResponse> content = List.of(commentResponse);
        Page<CommentResponse> mockPage = new PageImpl<>(content);

        // Mock 설정
        Mockito.when(communityCommentService.getComments(1L, 1, 10))
                .thenReturn(mockPage);

        // 요청 및 검증
        mockMvc.perform(get("/api/search/v1/communities/1/comments")
                        .param("page", "1")
                        .param("size", "10")
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].commentId").value(commentResponse.getCommentId()))
                .andExpect(jsonPath("$.data.content[0].content").value(commentResponse.getContent()))
                .andExpect(jsonPath("$.data.content[0].username").value(commentResponse.getUsername()));

        // Mock 호출 검증
        Mockito.verify(communityCommentService, Mockito.times(1)).getComments(1L, 1, 10);
    }





}
