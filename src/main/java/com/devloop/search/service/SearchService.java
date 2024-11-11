package com.devloop.search.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.CacheKeyGenerator;
import com.devloop.common.utils.SearchQueryUtil;
import com.devloop.community.entity.Community;
import com.devloop.community.service.CommunityService;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.service.LectureService;
import com.devloop.party.entity.Party;
import com.devloop.party.service.PartyService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegratedSearchPreview;
import com.devloop.search.response.IntegrationSearchResponse;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PartyService partyService;
    private final CommunityService communityService;
    private final LectureService lectureService;
    private final ProjectWithTutorService projectWithTutorService;
    private final RedisTemplate<String, String> rankingRedisTemplate;
    private static final String SEARCH_RANKING_KEY = "search:ranking";
    private static final int PREVIEW_SIZE = 5;

    @Value("${search.ranking.size:10}")
    private int rankingSize;


    //    @Cacheable(
//            value = "searchPreview",
//            key = "T(com.devloop.common.utils.CacheKeyGenerator).generateKey(#request)",
//            condition = "#request != null",
//            unless = "#result == null"
//    )
    public IntegratedSearchPreview integratedSearchPreview(IntegrationSearchRequest request) {
        log.info("Cache miss - executing search for key: {}",
                CacheKeyGenerator.generateKey(request));

        try {
//            // 검색 랭킹 업데이트
//            updateSearchRanking(request);

            BooleanBuilder partyCondition = SearchQueryUtil.buildSearchCondition(request, Party.class);
            BooleanBuilder communityCondition = SearchQueryUtil.buildSearchCondition(request, Community.class);
            BooleanBuilder pwtCondition = SearchQueryUtil.buildSearchCondition(request, ProjectWithTutor.class);
            BooleanBuilder lectureCondition = SearchQueryUtil.buildSearchCondition(request, Lecture.class);

            // 각각의 엔티티에 맞는 서비스 메서드 호출
            Page<IntegrationSearchResponse> partyResults = partyService.getPartyWithPage(
                    partyCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

            Page<IntegrationSearchResponse> communityResults = communityService.getCommunityWithPage(
                    communityCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

            Page<IntegrationSearchResponse> pwtResults = projectWithTutorService.getProjectWithTutorPage(
                    pwtCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

            Page<IntegrationSearchResponse> lectureResults = lectureService.getLectureWithPage(
                    lectureCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

            return IntegratedSearchPreview.builder()
                    .partyPreview(partyResults.getContent())
                    .communityPreview(communityResults.getContent())
                    .pwtPreview(pwtResults.getContent())
                    .lecturePreivew(lectureResults.getContent())
                    .totalCommunityCount(communityResults.getTotalElements())
                    .totalPartyCount(partyResults.getTotalElements())
                    .totalPwtCount(pwtResults.getTotalElements())
                    .totalLectureCount(lectureResults.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Error during search preview", e);
            throw e;
        }
    }

    //    @Cacheable(
//            value = "searchDetail",
//            key = "T(com.devloop.common.utils.CacheKeyGenerator).generateCategoryKey(#boardType, #page, #request)",
//            condition = "#request != null && #page > 0",
//            unless = "#result == null"
//    )
    public Page<IntegrationSearchResponse> searchByBoardType(
            IntegrationSearchRequest request, String boardType, int page, int size) {
        log.info("Cache miss - executing detailed search for boardType: {}, page: {}, key: {}",
                boardType, page, CacheKeyGenerator.generateCategoryKey(boardType, page, request));

//        // 검색 랭킹 업데이트
//        updateSearchRanking(request);

        request.setBoardType(boardType);
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        return switch (boardType.toLowerCase()) {
            case "party" -> searchParty(request, pageable);
            case "community" -> searchCommunity(request, pageable);
            case "pwt" -> searchPwt(request, pageable);
            case "lecture" -> searchLecture(request, pageable);
            default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        };
    }

//    //랭킹
//    public void incrementSearchCount(String keyword) {
//        ZSetOperations<String, String> zSetOps = rankingRedisTemplate.opsForZSet();
//        zSetOps.incrementScore(SEARCH_RANKING_KEY, keyword, 1);
//    }
//
//    //랭킹
//    public Set<ZSetOperations.TypedTuple<String>> getTopSearchKeywords() {
//        ZSetOperations<String, String> zSetOps = rankingRedisTemplate.opsForZSet();
//        return zSetOps.reverseRangeWithScores(SEARCH_RANKING_KEY, 0, rankingSize - 1);
//    }
//
//    //랭킹 시간 초기화
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void resetSearchRanking() {
//        rankingRedisTemplate.delete(SEARCH_RANKING_KEY); // 랭킹 초기화
//    }

    // 각 게시판별 검색 메서드
    private Page<IntegrationSearchResponse> searchParty(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Party.class);
        return partyService.getPartyWithPage(condition, pageable);
    }

    private Page<IntegrationSearchResponse> searchCommunity(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Community.class);
        return communityService.getCommunityWithPage(condition, pageable);
    }

    private Page<IntegrationSearchResponse> searchPwt(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, ProjectWithTutor.class);
        return projectWithTutorService.getProjectWithTutorPage(condition, pageable);
    }

    private Page<IntegrationSearchResponse> searchLecture(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Lecture.class);
        return lectureService.getLectureWithPage(condition, pageable);
    }


    //    private void updateSearchRanking(IntegrationSearchRequest request) {
//        // title 검색어 랭킹
//        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
//            incrementSearchCount("title:" + request.getTitle());
//        }
//
//        // content 검색어 랭킹
//        if (request.getContent() != null && !request.getContent().isEmpty()) {
//            incrementSearchCount("content:" + request.getContent());
//        }
//
//        // username 검색어 랭킹
//        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
//            incrementSearchCount("user:" + request.getUsername());
//        }
//
//        // category 검색어 랭킹
//        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
//            incrementSearchCount("category:" + request.getCategory());
//        }
//
//        // lecture 검색어 랭킹
//        if (request.getLecture() != null && !request.getLecture().isEmpty()) {
//            incrementSearchCount("lecture:" + request.getLecture());
//        }
//    }

}