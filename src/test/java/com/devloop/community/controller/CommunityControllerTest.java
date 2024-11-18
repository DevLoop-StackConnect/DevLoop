package com.devloop.community.controller;


import com.devloop.common.AuthUser;
import com.devloop.common.enums.Category;
import com.devloop.common.utils.JwtUtil;
import com.devloop.community.entity.ResolveStatus;
import com.devloop.community.request.CommunitySaveRequest;
import com.devloop.community.request.CommunityUpdateRequest;
import com.devloop.community.response.CommunityDetailResponse;
import com.devloop.community.response.CommunitySaveResponse;
import com.devloop.community.response.CommunitySimpleResponse;
import com.devloop.community.service.CommunityService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(CommunityController.class)
@AutoConfigureMockMvc
@Import({WebSecurityConfig.class, JwtUtil.class})
@TestPropertySource(properties = {
        "jwt.secret.key=7Iqk7YyM66W07YOA7L2U65Sp7YG065+9U3ByaW5n6rCV7J2Y7Yqc7YSw7LWc7JuQ67mI7J6F64uI64ukLg==" // JWT 테스트용 키
})
@MockBean(JpaMetamodelMappingContext.class)

public class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private JwtAuthenticationToken adminAuthenticationToken;
    private JwtAuthenticationToken userAuthenticationToken;

    @MockBean
    private CommunityService communityService;

    @BeforeEach
    public void setUp() {
        AuthUser adminUser = new AuthUser(1L, "admin@example.com", UserRole.ROLE_ADMIN);
        adminAuthenticationToken = new JwtAuthenticationToken(adminUser);

        AuthUser normalUser = new AuthUser(2L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);
    }


    @Test
    void createCommunity_성공() throws Exception {
        // Mock 데이터 준비
        CommunitySaveResponse mockResponse = CommunitySaveResponse.of(1L, "Test Title", "Test Content", "SOLVED", "Category", LocalDateTime.now());
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Test Content".getBytes());

//        Constructor<CommunitySaveRequest> constructor = CommunitySaveRequest.class.getDeclaredConstructor(String.class, String.class, Category.class);
//        constructor.setAccessible(true); // protected 접근 허용
//        CommunitySaveRequest expectedRequest = constructor.newInstance("Test Title", "Test Content", Category.APP_DEV);

        // Mock 설정
        Mockito.when(communityService.createCommunity(
                        Mockito.any(AuthUser.class),
                        Mockito.any(MockMultipartFile.class),
                        Mockito.any(CommunitySaveRequest.class)))
                .thenReturn(mockResponse);
        // 요청 및 검증
        mockMvc.perform(multipart("/api/v1/communities")
                        .file(file)
                        .param("title", "Test Title")
                        .param("content", "Test Content")
                        .param("category", "APP_DEV")
                        .with(authentication(adminAuthenticationToken)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.communityId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Title"));

    }

    @Test
    void getCommunities_성공() throws Exception {
        //Mock 데이터 준비
        List<CommunitySimpleResponse> mockCommunities = List.of(
                CommunitySimpleResponse.of(1L, "title1", "SOLVED", "APP_DEV"),
                CommunitySimpleResponse.of(2L, "title2", "RESOLVED", "WEB_DEV")
        );
        Page<CommunitySimpleResponse> mockPage = new PageImpl<>(mockCommunities, PageRequest.of(0, 10), mockCommunities.size());
        //Mock 설정
        Mockito.when(communityService.getCommunities(Mockito.eq(1), Mockito.eq(10))).thenReturn(mockPage);

        //요청, 검증
        mockMvc.perform(get("/api/search/v1/communities")
                        .param("page", "1")
                        .param("size", "10")
                        .with(authentication(userAuthenticationToken)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].communityId").value(1L))
                .andExpect(jsonPath("$.data.content[0].title").value("title1"))
                .andExpect(jsonPath("$.data.content[1].communityId").value(2L))
                .andExpect(jsonPath("$.data.content[1].title").value("title2"));

        // Mock 호출 검증
        Mockito.verify(communityService, Mockito.times(1))
                .getCommunities(1, 10);
    }

    @Test
    void getCommunity_성공() throws Exception {
        // Mock 데이터 준비
        CommunityDetailResponse mockResponse = CommunityDetailResponse.withAttachment(
                1L,
                "Test Title",
                "Test Content",
                "Open",
                "Category",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "http://example.com/image.jpg"
        );

        // Mock 설정
        Mockito.when(communityService.getCommunity(Mockito.eq(1L)))
                .thenReturn(mockResponse);

        // 요청 및 검증
        mockMvc.perform(get("/api/search/v1/communities/{communityId}", 1L)
                        .with(authentication(userAuthenticationToken)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.communityId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.content").value("Test Content"))
                .andExpect(jsonPath("$.data.imageUrl").value("http://example.com/image.jpg"));

        // Mock 호출 검증
        Mockito.verify(communityService, Mockito.times(1))
                .getCommunity(1L);

    }

    @Test
    void updateCommunity_성공() throws Exception {
        // Mock 데이터 준비
        CommunityDetailResponse mockResponse = CommunityDetailResponse.withoutAttachment(
                1L,
                "Updated Title",
                "Updated Content",
                "SOLVED",
                "Category",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        MockMultipartFile file = new MockMultipartFile("file", "updated.jpg", "image/jpeg", "Updated Content".getBytes());

        Constructor<CommunityUpdateRequest> constructor = CommunityUpdateRequest.class.getDeclaredConstructor(String.class, String.class, ResolveStatus.class ,Category.class);
        constructor.setAccessible(true); // protected 접근 허용
        CommunityUpdateRequest expectedRequest = constructor.newInstance("Test Title", "Test Content", ResolveStatus.SOLVED, Category.APP_DEV);

        // Mock 설정
        Mockito.when(communityService.updateCommunity(
                        Mockito.any(AuthUser.class),
                        Mockito.eq(1L),
                        Mockito.any(CommunityUpdateRequest.class),
                        Mockito.any(MultipartFile.class)))
                .thenReturn(mockResponse);

        // 요청 및 검증
        mockMvc.perform(multipart("/api/v1/communities/{communityId}", 1L)
                        .file(file)
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .param("status", "SOLVED")
                        .param("category", "APP_DEV")
                        .with(authentication(adminAuthenticationToken))
                        .with(request -> {
                            request.setMethod("PATCH"); // PATCH로 변경
                            return request;
                        }))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.communityId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Updated Title"))
                .andExpect(jsonPath("$.data.content").value("Updated Content"));

        // Mock 호출 검증
        Mockito.verify(communityService, Mockito.times(1))
                .updateCommunity(Mockito.any(AuthUser.class), Mockito.eq(1L), Mockito.any(CommunityUpdateRequest.class), Mockito.any(MultipartFile.class));
    }

    @Test
    void deleteCommunity_성공() throws Exception {
        // Mock 설정 (void 메서드에 대한 동작 설정은 필요하지 않음)
        Mockito.doNothing().when(communityService).deleteCommunity(Mockito.any(AuthUser.class), Mockito.eq(1L));

        // 요청 및 검증
        mockMvc.perform(delete("/api/v1/communities/{communityId}", 1L)
                        .with(authentication(adminAuthenticationToken)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isNoContent()); // HTTP 204 기대

        // Mock 호출 검증
        Mockito.verify(communityService, Mockito.times(1))
                .deleteCommunity(Mockito.any(AuthUser.class), Mockito.eq(1L));
    }




}
