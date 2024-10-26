package com.devloop.search.service;

import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import com.devloop.community.service.CommunityService;
import com.devloop.party.entity.Party;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.service.PartyService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private PartyService partyService;

    @Mock
    private CommunityService communityService;

    @Mock
    private ProjectWithTutorService projectWithTutorService;

    @InjectMocks
    private SearchService searchService;

    private IntegrationSearchRequest integrationSearchRequest;
    private List<IntegrationSearchResponse> partyResult;
    private List<IntegrationSearchResponse> communityResult;
    private List<IntegrationSearchResponse> pwtResult;

    private User user;
    private Community community;
    private ProjectWithTutor pwt;
    private Party party;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testMan")
                .email("test@test.com")
                .password("password")
                .userRole(UserRole.ROLE_USER)
                .attachmentId(1L)
                .build();

        community = Community.builder()
                .id(1L)
                .title("test community")
                .content("test content")
                .category(Category.WEB_DEV)
                .user(user)
                .resolveStatus(ResolveStatus.UNSOLVED)
                .boardType(BoardType.COMMUNITY)
                .communityComments(new ArrayList<>())
                .build();

        party = Party.builder()
                .id(1L)
                .title("test party")
                .contents("test content")
                .status(PartyStatus.IN_PROGRESS)
                .category(Category.WEB_DEV)
                .user(user)
                .boardType(BoardType.PARTY)
                .build();

        pwt = ProjectWithTutor.of(
                "test pwt",
                "test description",
                50000,
                LocalDateTime.now().plusDays(7),
                5,
                Level.EASY,
                Category.WEB_DEV,
                user
        );

        integrationSearchRequest = new IntegrationSearchRequest();
        integrationSearchRequest.setBoardType(BoardType.COMMUNITY.name().toLowerCase());
        integrationSearchRequest.setTitle("test title");
        integrationSearchRequest.setUsername("testMan");
        integrationSearchRequest.setCategory(Category.WEB_DEV.name());

        partyResult = List.of(IntegrationSearchResponse.of(party, BoardType.PARTY.name()));
        communityResult = List.of(IntegrationSearchResponse.of(community, BoardType.COMMUNITY.name()));
        pwtResult = List.of(IntegrationSearchResponse.of(pwt, BoardType.PWT.name()));
    }

    @Test
    void 커뮤니티_검색_성공여부() {
        // given
        integrationSearchRequest.setBoardType("community");
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        doReturn(new PageImpl<>(communityResult))
                .when(communityService)
                .getCommunityWithPage(any(Specification.class), eq(pageable));

        // when
        Page<IntegrationSearchResponse> result = searchService.integrationSearch(integrationSearchRequest, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(communityService).getCommunityWithPage(any(Specification.class), eq(pageable));
    }

    @Test
    void 파티_검색_성공여부() {
        // given
        integrationSearchRequest.setBoardType("party");
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        doReturn(new PageImpl<>(partyResult))
                .when(partyService)
                .getPartyWithPage(any(Specification.class), eq(pageable));

        // when
        Page<IntegrationSearchResponse> result = searchService.integrationSearch(integrationSearchRequest, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(partyService).getPartyWithPage(any(Specification.class), eq(pageable));
    }

    @Test
    void PWT_검색_성공여부() {
        // given
        integrationSearchRequest.setBoardType("project");  // PWT는 project로 검색해야 함
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        doReturn(new PageImpl<>(pwtResult))
                .when(projectWithTutorService)
                .getProjectWithTutorPage(any(Specification.class), eq(pageable));

        // when
        Page<IntegrationSearchResponse> result = searchService.integrationSearch(integrationSearchRequest, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(projectWithTutorService).getProjectWithTutorPage(any(Specification.class), eq(pageable));
    }

    @Test
    void 통합검색_성공여부() {
        // given
        integrationSearchRequest.setBoardType("");  // 빈 문자열로 설정하여 통합검색
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        doReturn(partyResult)
                .when(partyService)
                .getParty(any(Specification.class));

        doReturn(communityResult)
                .when(communityService)
                .getAllCommunity(any(Specification.class));

        doReturn(pwtResult)
                .when(projectWithTutorService)
                .getProjectWithTutor(any(Specification.class));

        // when
        Page<IntegrationSearchResponse> result = searchService.integrationSearch(integrationSearchRequest, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        verify(communityService).getAllCommunity(any(Specification.class));
        verify(partyService).getParty(any(Specification.class));
        verify(projectWithTutorService).getProjectWithTutor(any(Specification.class));
    }

    @Test
    void 잘못된_게시판_타입_검색시_예외발생() {
        // given
        integrationSearchRequest.setBoardType("INVALID_TYPE");

        // when & then
        assertThrows(ApiException.class, () ->
                searchService.integrationSearch(integrationSearchRequest, 1, 10)
        );

        verifyNoInteractions(communityService, partyService, projectWithTutorService);
    }

    @Test
    void 페이지_파라미터가_음수일때_예외발생() {
        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                searchService.integrationSearch(integrationSearchRequest, 0, 10)  // 1미만이면 예외 발생
        );
    }

    @Test
    void 검색어가_null일때_전체_검색() {
        // given
        integrationSearchRequest.setTitle(null);
        integrationSearchRequest.setUsername(null);
        integrationSearchRequest.setCategory(null);
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        doReturn(new PageImpl<>(communityResult))
                .when(communityService)
                .getCommunityWithPage(any(Specification.class), eq(pageable));

        // when
        Page<IntegrationSearchResponse> result = searchService.integrationSearch(integrationSearchRequest, 1, 10);

        // then
        assertNotNull(result);
        verify(communityService).getCommunityWithPage(any(Specification.class), eq(pageable));
    }
}