package com.devloop.search.service;

import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchQueryUtil;
import com.devloop.community.entity.Community;
import com.devloop.community.entity.ResolveStatus;
import com.devloop.community.service.CommunityService;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.service.LectureService;
import com.devloop.party.entity.Party;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.service.PartyService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.enums.Level;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegratedSearchPreview;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.search.service.SearchService;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.querydsl.core.BooleanBuilder;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock(lenient = true)
    private PartyService partyService;
    @Mock(lenient = true)
    private CommunityService communityService;
    @Mock(lenient = true)
    private ProjectWithTutorService projectWithTutorService;
    @Mock(lenient = true)
    private LectureService lectureService;
    @Mock(lenient = true)
    private RedisTemplate<String, String> rankingRedisTemplate;

    @InjectMocks
    private SearchService searchService;

    private IntegrationSearchRequest request;
    private List<IntegrationSearchResponse> partyResults;
    private List<IntegrationSearchResponse> communityResults;
    private List<IntegrationSearchResponse> pwtResults;
    private List<IntegrationSearchResponse> lectureResults;

    private User user;
    private Community community;
    private ProjectWithTutor pwt;
    private Party party;
    private Lecture lecture;

    @SuppressWarnings("unchecked")
    private ZSetOperations<String, String> zSetOperations = mock(ZSetOperations.class);

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
                .title("test Community")
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
                BigDecimal.valueOf(50000),
                LocalDateTime.now().plusDays(7),
                5,
                Level.EASY,
                Category.WEB_DEV,
                user
        );

        lecture = Lecture.builder()
                .id(1L)
                .description("test description")
                .recommend("추천대상")
                .category(Category.WEB_DEV)
                .level(Level.EASY)
                .approval(Approval.WAITE)
                .user(user)
                .lectureVideos(new ArrayList<>())
                .lectureReviews(new ArrayList<>())
                .build();

        request = IntegrationSearchRequest.builder()
                .boardType(null)
                .title("test")
                .content("test content")
                .username("testMan")
                .category(Category.WEB_DEV.name())
                .lecture(null)
                .build();

        partyResults = List.of(IntegrationSearchResponse.of(party, BoardType.PARTY.name()));
        communityResults = List.of(IntegrationSearchResponse.of(community, BoardType.COMMUNITY.name()));
        pwtResults = List.of(IntegrationSearchResponse.of(pwt, BoardType.PWT.name()));
        lectureResults = List.of(IntegrationSearchResponse.of(lecture, "LECTURE"));

        when(rankingRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void 통합검색_프리뷰_성공() {
        // given
        PageRequest previewPageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        BooleanBuilder partyCondition = SearchQueryUtil.buildSearchCondition(request, Party.class);
        BooleanBuilder communityCondition = SearchQueryUtil.buildSearchCondition(request, Community.class);
        BooleanBuilder pwtCondition = SearchQueryUtil.buildSearchCondition(request, ProjectWithTutor.class);
        BooleanBuilder lectureCondition = SearchQueryUtil.buildSearchCondition(request, Lecture.class);

        when(partyService.getPartyWithPage(eq(partyCondition), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(partyResults, previewPageable, partyResults.size()));
        when(communityService.getCommunityWithPage(eq(communityCondition), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(communityResults, previewPageable, communityResults.size()));
        when(projectWithTutorService.getProjectWithTutorPage(eq(pwtCondition), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(pwtResults, previewPageable, pwtResults.size()));
        when(lectureService.getLectureWithPage(eq(lectureCondition), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(lectureResults, previewPageable, lectureResults.size()));

        // when
        IntegratedSearchPreview result = searchService.integratedSearchPreview(request);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPartyPreview().size());
        assertEquals(1, result.getCommunityPreview().size());
        assertEquals(1, result.getPwtPreview().size());
        assertEquals(1, result.getLecturePreivew().size());
    }

    @Test
    void 카테고리별_검색_파티_성공() {
        // given
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Party.class);

        when(partyService.getPartyWithPage(eq(condition), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(partyResults, pageable, partyResults.size()));

        // when
        Page<IntegrationSearchResponse> result = searchService.searchByBoardType(request, "party", 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(partyService).getPartyWithPage(eq(condition), any(PageRequest.class));
    }

    @Test
    void 카테고리별_검색_커뮤니티_성공() {
        // given
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Community.class);

        when(communityService.getCommunityWithPage(eq(condition), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(communityResults, pageable, communityResults.size()));

        // when
        Page<IntegrationSearchResponse> result = searchService.searchByBoardType(request, "community", 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(communityService).getCommunityWithPage(eq(condition), any(PageRequest.class));
    }

    @Test
    void 잘못된_카테고리_검색시_예외발생() {
        // when & then
        assertThrows(ApiException.class, () ->
                searchService.searchByBoardType(request, "invalid", 1, 10)
        );
    }
}