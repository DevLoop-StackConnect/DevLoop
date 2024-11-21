package com.devloop.search.service;


import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.CacheKeyGenerator;
import com.devloop.common.utils.CacheablePage;
import com.devloop.common.utils.SearchQueryUtil;
import com.devloop.community.entity.Community;
import com.devloop.community.event.CommunityCreatedEvent;
import com.devloop.community.event.CommunityDeletedEvent;
import com.devloop.community.event.CommunityUpdatedEvent;
import com.devloop.community.repository.elasticsearch.CommunityElasticsearchRepository;
import com.devloop.community.service.CommunityService;
import com.devloop.lecture.entity.Lecture;
import com.devloop.lecture.event.LectureCreatedEvent;
import com.devloop.lecture.event.LectureDeletedEvent;
import com.devloop.lecture.event.LectureUpdatedEvent;
import com.devloop.lecture.repository.elasticsearch.LectureElasticsearchRepository;
import com.devloop.lecture.service.LectureService;
import com.devloop.party.entity.Party;
import com.devloop.party.event.PartyCreatedEvent;
import com.devloop.party.event.PartyDeletedEvent;
import com.devloop.party.event.PartyUpdatedEvent;
import com.devloop.party.repository.elasticsearch.PartyElasticsearchRepository;
import com.devloop.party.service.PartyService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.event.PwtCreatedEvent;
import com.devloop.pwt.event.PwtDeletedEvent;
import com.devloop.pwt.event.PwtUpdatedEvent;
import com.devloop.pwt.repository.elasticsearch.ProjectWithTutorElasticsearchRepository;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegratedSearchPreview;
import com.devloop.search.response.IntegrationSearchResponse;
import com.querydsl.core.BooleanBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class SearchService {
    //DB 검색
    private final PartyService partyService;
    private final CommunityService communityService;
    private final LectureService lectureService;
    private final ProjectWithTutorService projectWithTutorService;
    //Elasticsearch 검색용
    private final PartyElasticsearchRepository partySearchRepository;
    private final CommunityElasticsearchRepository communitySearchRepository;
    private final LectureElasticsearchRepository lectureSearchRepository;
    private final ProjectWithTutorElasticsearchRepository pwtSearchRepository;

    private final RedisTemplate<String, String> rankingRedisTemplate;
    private final ElasticsearchOperations elasticsearchOperations;
    private static final String SEARCH_RANKING_KEY = "search:ranking"; //검색 랭킹 키 값
    private static final int PREVIEW_SIZE = 5; //미리보기 사이즈

    @Value("${search.ranking.size:10}")
    private int rankingSize;  //랭킹 사이즈 10개

    @Value("${search.elasticsearch.enabled}")
    private boolean elasticsearchEnabled; //Elasticsearch 활성화 여부 가져오기


    @Cacheable(
            value = "searchPreview",
            key = "T(com.devloop.common.utils.CacheKeyGenerator).generateKey(#request)",
            condition = "#request != null",
            unless = "#result == null"
    )
    public IntegratedSearchPreview integratedSearchPreview(IntegrationSearchRequest request) {
        log.info("Entering integratedSearchPreview method in SearchService");
        log.info("Starting integrated search preview with request: {}", request);

        try {
            updateSearchRanking(request);
            return elasticsearchEnabled ?
                    searchWithElasticsearch(request) :
                    searchWithDatabase(request);
        } catch (Exception e) {
            log.error("Error during elasticsearch search, falling back to database", e);
            return searchWithDatabase(request);
        }
    }

    @Cacheable(
            value = "searchDetail",
            key = "T(com.devloop.common.utils.CacheKeyGenerator).generateCategoryKey(#boardType, #page, #request)",
            condition = "#request != null && #page > 0",
            unless = "#result == null"
    )
    public CacheablePage<IntegrationSearchResponse> searchByBoardType(
            IntegrationSearchRequest request,
            String boardType,
            int page,
            int size
    ) {
        try {
            updateSearchRanking(request);
            Page<IntegrationSearchResponse> searchResult = elasticsearchEnabled ?
                    searchByBoardTypeWithElasticSearch(request, boardType, page, size) :
                    searchByBoardTypeWithDatabase(request, boardType, page, size);
            return new CacheablePage<>(searchResult);
        } catch (Exception e) {
            log.error("Error during elasticsearch search, falling back to database", e);
            Page<IntegrationSearchResponse> dbResult =
                    searchByBoardTypeWithDatabase(request, boardType, page, size);
            return new CacheablePage<>(dbResult);
        }
    }

    private IntegratedSearchPreview searchWithElasticsearch(IntegrationSearchRequest request) {
        log.info("Executing elasticsearch search with request: {}", request);

        try {
            if (StringUtils.hasText(request.getBoardType())) {
                return executeSpecificBoardTypeSearch(request);
            }
            log.info("elasticsearch");
            return executeIntegratedSearch(request);
        } catch (Exception e) {
            log.error("Error during elasticsearch search", e);
            throw new ApiException(ErrorStatus._ELASTICSEARCH_ERROR);
        }
    }

    private IntegratedSearchPreview executeSpecificBoardTypeSearch(IntegrationSearchRequest request) {
        log.info("Executing specific board type search for: {}", request.getBoardType());

        NativeQuery query = buildSearchQuery(request, request.getBoardType(), 0, PREVIEW_SIZE);
        log.debug("Built query: {}", query);
        return switch (request.getBoardType().toLowerCase()) {
            case "party" -> {
                SearchHits<Party> hits = elasticsearchOperations.search(query, Party.class);
                log.info("Party search result - total hits: {}", hits.getTotalHits());
                yield IntegratedSearchPreview.builder()
                        .partyPreview(convertToSearchResponse(hits))
                        .totalPartyCount(hits.getTotalHits())
                        .build();
            }
            case "community" -> {
                SearchHits<Community> hits = elasticsearchOperations.search(query, Community.class);
                log.info("Community search result - total hits: {}", hits.getTotalHits());
                yield IntegratedSearchPreview.builder()
                        .communityPreview(convertToSearchResponse(hits))
                        .totalCommunityCount(hits.getTotalHits())
                        .build();
            }
            case "pwt" -> {
                SearchHits<ProjectWithTutor> hits = elasticsearchOperations.search(query, ProjectWithTutor.class);
                log.info("Pwt search result - total hits: {}", hits.getTotalHits());
                yield IntegratedSearchPreview.builder()
                        .pwtPreview(convertToSearchResponse(hits))
                        .totalPwtCount(hits.getTotalHits())
                        .build();
            }
            case "lecture" -> {
                SearchHits<Lecture> hits = elasticsearchOperations.search(query, Lecture.class);
                log.info("Lecture search result - total hits: {}", hits.getTotalHits());
                yield IntegratedSearchPreview.builder()
                        .lecturePreivew(convertToSearchResponse(hits))
                        .totalLectureCount(hits.getTotalHits())
                        .build();
            }
            default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        };
    }

    private IntegratedSearchPreview executeIntegratedSearch(IntegrationSearchRequest request) {
        log.info("Starting integrated search across all board types.");
        NativeQuery partyQuery = buildSearchQuery(request, "PARTY", 0, PREVIEW_SIZE);
        NativeQuery communityQuery = buildSearchQuery(request, "COMMUNITY", 0, PREVIEW_SIZE);
        NativeQuery pwtQuery = buildSearchQuery(request, "PWT", 0, PREVIEW_SIZE);
        NativeQuery lectureQuery = buildSearchQuery(request, "LECTURE", 0, PREVIEW_SIZE);

        SearchHits<Party> partyHits = elasticsearchOperations.search(partyQuery, Party.class);
        SearchHits<Community> communityHits = elasticsearchOperations.search(communityQuery, Community.class);
        SearchHits<ProjectWithTutor> pwtHits = elasticsearchOperations.search(pwtQuery, ProjectWithTutor.class);
        SearchHits<Lecture> lectureHits = elasticsearchOperations.search(lectureQuery, Lecture.class);

        return IntegratedSearchPreview.builder()
                .partyPreview(convertToSearchResponse(partyHits))
                .communityPreview(convertToSearchResponse(communityHits))
                .pwtPreview(convertToSearchResponse(pwtHits))
                .lecturePreivew(convertToSearchResponse(lectureHits))
                .totalPartyCount(partyHits.getTotalHits())
                .totalCommunityCount(communityHits.getTotalHits())
                .totalPwtCount(pwtHits.getTotalHits())
                .totalLectureCount(lectureHits.getTotalHits())
                .build();
    }


    private Page<IntegrationSearchResponse> searchByBoardTypeWithElasticSearch(
            IntegrationSearchRequest request,
            String boardType,
            int page,
            int size) {
        try {
            NativeQuery searchQuery = buildSearchQuery(request, boardType.toLowerCase(), page - 1, size);

            return switch (boardType.toLowerCase()) {
                case "pwt" -> {
                    SearchHits<ProjectWithTutor> searchHits = elasticsearchOperations.search(searchQuery, ProjectWithTutor.class);
                    List<IntegrationSearchResponse> responses = searchHits.stream()
                            .map(hit -> {
                                ProjectWithTutor pwt = hit.getContent();
                                return IntegrationSearchResponse.builder()
                                        .id(pwt.getId())
                                        .boardType("pwt")
                                        .title(pwt.getTitle())
                                        .content(pwt.getDescription())
                                        .category(pwt.getCategory().toString())
                                        .username(pwt.getUser().getUsername())
                                        .createdAt(pwt.getCreatedAt())
                                        .score(hit.getScore())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    yield new PageImpl<>(responses, PageRequest.of(page - 1, size), searchHits.getTotalHits());
                }
                case "lecture" -> {
                    SearchHits<Lecture> searchHits = elasticsearchOperations.search(searchQuery, Lecture.class);
                    List<IntegrationSearchResponse> responses = searchHits.stream()
                            .map(hit -> {
                                Lecture lecture = hit.getContent();
                                return IntegrationSearchResponse.builder()
                                        .id(lecture.getId())
                                        .boardType("lecture")
                                        .title(lecture.getTitle())
                                        .content(lecture.getDescription())
                                        .category(lecture.getCategory().toString())
                                        .username(lecture.getUser().getUsername())
                                        .createdAt(lecture.getCreatedAt())
                                        .score(hit.getScore())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    yield new PageImpl<>(responses, PageRequest.of(page - 1, size), searchHits.getTotalHits());
                }
                case "community" -> {
                    SearchHits<Community> searchHits = elasticsearchOperations.search(searchQuery, Community.class);
                    List<IntegrationSearchResponse> responses = searchHits.stream()
                            .map(hit -> {
                                Community community = hit.getContent();
                                return IntegrationSearchResponse.builder()
                                        .id(community.getId())
                                        .boardType("community")
                                        .title(community.getTitle())
                                        .content(community.getContent())
                                        .category(community.getCategory().toString())
                                        .username(community.getUser().getUsername())
                                        .createdAt(community.getCreatedAt())
                                        .score(hit.getScore())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    yield new PageImpl<>(responses, PageRequest.of(page - 1, size), searchHits.getTotalHits());
                }
                case "party" -> {
                    SearchHits<Party> searchHits = elasticsearchOperations.search(searchQuery, Party.class);
                    List<IntegrationSearchResponse> responses = searchHits.stream()
                            .map(hit -> {
                                Party party = hit.getContent();
                                return IntegrationSearchResponse.builder()
                                        .id(party.getId())
                                        .boardType("party")
                                        .title(party.getTitle())
                                        .content(party.getContents())
                                        .category(party.getCategory().toString())
                                        .username(party.getUser().getUsername())
                                        .createdAt(party.getCreatedAt())
                                        .score(hit.getScore())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    yield new PageImpl<>(responses, PageRequest.of(page - 1, size), searchHits.getTotalHits());
                }
                default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
            };
        } catch (Exception e) {
            log.error("Elasticsearch search error: ", e);
            throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        }
    }

    private NativeQuery buildSearchQuery(IntegrationSearchRequest request, String boardType, int page, int size) {
        log.debug("Building search query for boardType: {}, page: {}, size: {}", boardType, page, size);

        // Logging request details
        log.debug("IntegrationSearchRequest: {}", request);

        var query = Query.of(q -> q
                .bool(b -> {
                    // Exact match conditions
                    if (StringUtils.hasText(boardType)) {
                        log.debug("Adding exact match for board_type: {}", boardType.toUpperCase());
                        b.must(must -> must
                                .term(t -> t
                                        .field("board_type")
                                        .value(boardType.toUpperCase())));
                    }
                    if (StringUtils.hasText(request.getCategory())) {
                        log.debug("Adding exact match for category: {}", request.getCategory().toUpperCase());
                        b.must(must -> must
                                .term(t -> t
                                        .field("category")
                                        .value(request.getCategory().toUpperCase())));
                    }

                    // Partial match conditions
                    var shouldQueries = new ArrayList<Query>();
                    if (StringUtils.hasText(request.getTitle())) {
                        log.debug("Adding partial match for title: {}", request.getTitle());
                        shouldQueries.addAll(Arrays.asList(
                                // `match` 쿼리로 부분 검색 지원
                                Query.of(sq -> sq
                                        .match(m -> m
                                                .field("title")
                                                .query(request.getTitle())
                                                .fuzziness("AUTO") // 오타 허용
                                                .analyzer("korean_and_english"))),
                                // `multi_match`로 여러 필드에서 부분 검색
                                Query.of(sq -> sq
                                        .multiMatch(mm -> mm
                                                .query(request.getTitle())
                                                .fields(List.of("title", "title.keyword"))
                                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)))
                        ));
                    }

                    if (StringUtils.hasText(request.getContent())) {
                        log.debug("Adding partial match for content: {}", request.getContent());
                        List<Query> contentQueries = new ArrayList<>();

                        // Community: content field
                        contentQueries.addAll(Arrays.asList(
                                Query.of(sq -> sq
                                        .match(m -> m
                                                .field("content")
                                                .query(request.getContent())
                                                .analyzer("korean_and_english"))),
                                Query.of(sq -> sq
                                        .matchPhrase(m -> m
                                                .field("content")
                                                .query(request.getContent()))),
                                Query.of(sq -> sq
                                        .fuzzy(f -> f
                                                .field("content")
                                                .value(request.getContent())
                                                .fuzziness("AUTO")))
                        ));

                        // Party: contents field
                        contentQueries.addAll(Arrays.asList(
                                Query.of(sq -> sq
                                        .match(m -> m
                                                .field("contents")
                                                .query(request.getContent())
                                                .analyzer("korean_and_english"))),
                                Query.of(sq -> sq
                                        .matchPhrase(m -> m
                                                .field("contents")
                                                .query(request.getContent()))),
                                Query.of(sq -> sq
                                        .fuzzy(f -> f
                                                .field("contents")
                                                .value(request.getContent())
                                                .fuzziness("AUTO")))
                        ));

                        // Lecture & PWT: description field
                        contentQueries.addAll(Arrays.asList(
                                Query.of(sq -> sq
                                        .match(m -> m
                                                .field("description")
                                                .query(request.getContent())
                                                .analyzer("korean_and_english"))),
                                Query.of(sq -> sq
                                        .matchPhrase(m -> m
                                                .field("description")
                                                .query(request.getContent()))),
                                Query.of(sq -> sq
                                        .fuzzy(f -> f
                                                .field("description")
                                                .value(request.getContent())
                                                .fuzziness("AUTO")))
                        ));
                        shouldQueries.addAll(contentQueries);
                    }

                    if (StringUtils.hasText(request.getUsername())) {
                        log.debug("Adding partial match for username: {}", request.getUsername());
                        shouldQueries.addAll(Arrays.asList(
                                Query.of(sq -> sq
                                        .match(m -> m
                                                .field("user.username")
                                                .query(request.getUsername())
                                                .analyzer("korean_and_english"))),
                                Query.of(sq -> sq
                                        .wildcard(w -> w
                                                .field("user.username.keyword")
                                                .wildcard("*" + request.getUsername().toLowerCase() + "*")))
                        ));
                    }

                    // Add should queries to must clause
                    if (!shouldQueries.isEmpty()) {
                        log.debug("Adding should queries: {}", shouldQueries);
                        b.must(must -> must
                                .bool(bq -> bq
                                        .should(shouldQueries)
                                        .minimumShouldMatch("1")));
                    }
                    return b;
                }));

        log.debug("Final Elasticsearch query object: {}", query);

        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(page, size))
                .withSort(Sort.by(Sort.Direction.DESC, "created_at"))
                .build();
    }

    private <T> List<IntegrationSearchResponse> convertToSearchResponse(SearchHits<T> searchHits) {
        return searchHits.stream()
                .map(hit -> {
                    String idValue = hit.getId(); // Elasticsearch document ID
                    Long id = null;
                    try {
                        id = Long.parseLong(idValue);
                    } catch (NumberFormatException e) {
                        log.warn("Failed to parse ID: {}", idValue);
                    }

                    T content = hit.getContent();
                    IntegrationSearchResponse response = IntegrationSearchResponse.of(content, hit.getScore());

                    // ID 설정
                    if (id != null) {
                        response = IntegrationSearchResponse.builder()
                                .id(id)
                                .boardType(response.getBoardType())
                                .title(response.getTitle())
                                .content(response.getContent())
                                .category(response.getCategory())
                                .username(response.getUsername())
                                .createdAt(response.getCreatedAt())
                                .score(response.getScore())
                                .build();
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }


    private IntegratedSearchPreview searchWithDatabase(IntegrationSearchRequest request) {
        log.info("Cache miss - executing search for key: {}",
                CacheKeyGenerator.generateKey(request));

        try {
            // 검색 랭킹 업데이트
            updateSearchRanking(request);
            //각 엔티티 검색 조건 빌드
            BooleanBuilder partyCondition = SearchQueryUtil.buildSearchCondition(request, Party.class);
            BooleanBuilder communityCondition = SearchQueryUtil.buildSearchCondition(request, Community.class);
            BooleanBuilder pwtCondition = SearchQueryUtil.buildSearchCondition(request, ProjectWithTutor.class);
            BooleanBuilder lectureCondition = SearchQueryUtil.buildSearchCondition(request, Lecture.class);

            // 조건이 없으면 빈 결과 반환
            if (!partyCondition.hasValue() && !communityCondition.hasValue()
                    && !pwtCondition.hasValue() && !lectureCondition.hasValue()) {
                return new IntegratedSearchPreview();
            }

            // 각각의 엔티티에 맞는 서비스 메서드 호출
            Page<IntegrationSearchResponse> partyResults = partyService.getPartyWithPage(
                    partyCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));
            Page<IntegrationSearchResponse> communityResults = communityService.getCommunityWithPage(
                    communityCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));
            Page<IntegrationSearchResponse> pwtResults = projectWithTutorService.getProjectWithTutorPage(
                    pwtCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));
            Page<IntegrationSearchResponse> lectureResults = lectureService.getLectureWithPage(
                    lectureCondition, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

            log.info("databaseSearch");
            return IntegratedSearchPreview.builder()
                    .partyPreview(partyResults.getContent())
                    .communityPreview(communityResults.getContent())
                    .pwtPreview(pwtResults.getContent())
                    .lecturePreivew(lectureResults.getContent())
                    .totalPartyCount(partyResults.getTotalElements())
                    .totalCommunityCount(communityResults.getTotalElements())
                    .totalPwtCount(pwtResults.getTotalElements())
                    .totalLectureCount(lectureResults.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Error during search preview", e);
            throw e;
        }
    }


    private Page<IntegrationSearchResponse> searchByBoardTypeWithDatabase(
            IntegrationSearchRequest request, String boardType, int page, int size) {
        try {
            // 검색 랭킹 업데이트
            updateSearchRanking(request);

            request.setBoardType(boardType);
            PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

            return switch (boardType.toLowerCase()) {
                case "party" -> searchParty(request, pageable);
                case "community" -> searchCommunity(request, pageable);
                case "pwt" -> searchPwt(request, pageable);
                case "lecture" -> searchLecture(request, pageable);
                default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
            };
        } catch (Exception e) {
            log.error("Error during search by board type", e);
            throw e;
        }
    }


    //랭킹
    public Set<ZSetOperations.TypedTuple<String>> getTopSearchKeywords() {
        ZSetOperations<String, String> zSetOps = rankingRedisTemplate.opsForZSet();
        return zSetOps.reverseRangeWithScores(SEARCH_RANKING_KEY, 0, rankingSize - 1);
    }

    //랭킹
    public void incrementSearchCount(String keyword) {
        ZSetOperations<String, String> zSetOps = rankingRedisTemplate.opsForZSet();
        zSetOps.incrementScore(SEARCH_RANKING_KEY, keyword, 1);
    }

    // 각 게시판별 검색 메서드
    private Page<IntegrationSearchResponse> searchParty(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Party.class);
        log.debug("Party search condition: {}", condition);
        return partyService.getPartyWithPage(condition, pageable);
    }

    private Page<IntegrationSearchResponse> searchCommunity(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Community.class);
        log.debug("Community search condition: {}", condition);
        return communityService.getCommunityWithPage(condition, pageable);
    }

    private Page<IntegrationSearchResponse> searchPwt(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, ProjectWithTutor.class);
        log.debug("PWT search condition: {}", condition);
        return projectWithTutorService.getProjectWithTutorPage(condition, pageable);
    }

    private Page<IntegrationSearchResponse> searchLecture(IntegrationSearchRequest request, PageRequest pageable) {
        BooleanBuilder condition = SearchQueryUtil.buildSearchCondition(request, Lecture.class);
        log.debug("Lecture search condition: {}", condition);
        return lectureService.getLectureWithPage(condition, pageable);
    }

    //랭킹 시간 초기화
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetSearchRanking() {
        rankingRedisTemplate.delete(SEARCH_RANKING_KEY); // 랭킹 초기화
    }

    private void updateSearchRanking(IntegrationSearchRequest request) {
        // title 검색어 랭킹
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            incrementSearchCount("title:" + request.getTitle());
        }

        // content 검색어 랭킹
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            incrementSearchCount("content:" + request.getContent());
        }

        // username 검색어 랭킹
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            incrementSearchCount("user:" + request.getUsername());
        }

        // category 검색어 랭킹
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            incrementSearchCount("category:" + request.getCategory());
        }

        // lecture 검색어 랭킹
        if ("lecture".equalsIgnoreCase(request.getBoardType()) && request.getTitle() != null && !request.getTitle().isEmpty()) {
            incrementSearchCount("lecture:" + request.getTitle());
        }
    }

    // 데이터 동기화 이벤트 핸들러
    @EventListener
    public void handlePartyCreatedEvent(PartyCreatedEvent event) {
        Party party = event.getParty();
        partySearchRepository.save(party);
    }

    @EventListener
    public void handlePartyUpdatedEvent(PartyUpdatedEvent event) {
        Party party = event.getParty();
        partySearchRepository.save(party);
    }

    @EventListener
    public void handlePartyDeletedEvent(PartyDeletedEvent event) {
        partySearchRepository.deleteById(event.getParty().getId());
    }


    // Community 이벤트 핸들러
    @EventListener
    public void handleCommunityCreatedEvent(CommunityCreatedEvent event) {
        Community community = event.getCommunity();
        communitySearchRepository.save(community);
    }

    @EventListener
    public void handleCommunityUpdatedEvent(CommunityUpdatedEvent event) {
        Community community = event.getCommunity();
        communitySearchRepository.save(community);
    }

    @EventListener
    public void handleCommunityDeletedEvent(CommunityDeletedEvent event) {
        communitySearchRepository.deleteById(event.getCommunity().getId());
    }

    // PWT 이벤트 핸들러
    @EventListener
    public void handlePwtCreatedEvent(PwtCreatedEvent event) {
        ProjectWithTutor pwt = event.getProjectWithTutor();
        pwtSearchRepository.save(pwt);
    }

    @EventListener
    public void handlePwtUpdatedEvent(PwtUpdatedEvent event) {
        ProjectWithTutor pwt = event.getProjectWithTutor();
        pwtSearchRepository.save(pwt);
    }

    @EventListener
    public void handlePwtDeletedEvent(PwtDeletedEvent event) {
        pwtSearchRepository.deleteById(event.getProjectWithTutor().getId());
    }

    // Lecture 이벤트 핸들러
    @EventListener
    public void handleLectureCreatedEvent(LectureCreatedEvent event) {
        Lecture lecture = event.getLecture();
        lectureSearchRepository.save(lecture);
    }

    @EventListener
    public void handleLectureUpdatedEvent(LectureUpdatedEvent event) {
        Lecture lecture = event.getLecture();
        lectureSearchRepository.save(lecture);
    }

    @EventListener
    public void handleLectureDeletedEvent(LectureDeletedEvent event) {
        lectureSearchRepository.deleteById(event.getLecture().getId());
    }

    // 전체 데이터 초기 동기화
    @PostConstruct
    public void initializeElasticsearchData() {
        log.info("Starting Elasticsearch data synchronization");

        syncDataWithPagination(
                pageRequest -> lectureService.findAllWithPagination(pageRequest),
                lectureSearchRepository,
                "LECTURE"
        );

        syncDataWithPagination(
                pageRequest -> partyService.findAllWithPagination(pageRequest),
                partySearchRepository,
                "PARTY"
        );

        syncDataWithPagination(
                pageRequest -> communityService.findAllWithPagination(pageRequest),
                communitySearchRepository,
                "COMMUNITY"
        );

        syncDataWithPagination(
                pageRequest -> projectWithTutorService.findAllWithPagination(pageRequest),
                pwtSearchRepository,
                "PWT"
        );

        log.info("Elasticsearch data synchronization completed");
    }

    private <T, S extends ElasticsearchRepository<T, Long>> void syncDataWithPagination(
            Function<PageRequest, Page<T>> fetchPageFunction,
            ElasticsearchRepository<T, Long> repository,
            String boardType
    ) {
        try {
            int page = 0;
            int pageSize = 100;
            Page<T> dataPage;

            do {
                dataPage = fetchPageFunction.apply(PageRequest.of(page, pageSize));
                if (!dataPage.isEmpty()) {
                    log.debug("Fetched page {} with {} items for {}", page, dataPage.getContent().size(), repository.getClass().getSimpleName());

                    List<T> updatedItems = dataPage.getContent().stream()
                            .map(item -> ensureBoardType(item, boardType))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    repository.saveAll(updatedItems);
                    log.debug("Saved {} items to Elasticsearch for {}", updatedItems.size(), repository.getClass().getSimpleName());
                }
                page++;
            } while (dataPage.hasNext());
        } catch (Exception e) {
            log.error("Error during data synchronization for {}: ", repository.getClass().getSimpleName(), e);
            throw e;
        }
    }

    private <T> T ensureBoardType(T entity, String boardType) {
        if (entity instanceof Lecture lecture && lecture.getBoardType() == null) {
            return (T) Lecture.builder()
                    .id(lecture.getId())
                    .description(lecture.getDescription())
                    .recommend(lecture.getRecommend())
                    .category(lecture.getCategory())
                    .level(lecture.getLevel())
                    .user(lecture.getUser())
                    .boardType(BoardType.valueOf(boardType))
                    .build();
        } else if (entity instanceof Party party && party.getBoardType() == null) {
            return (T) Party.builder()
                    .id(party.getId())
                    .title(party.getTitle())
                    .contents(party.getContents())
                    .status(party.getStatus())
                    .category(party.getCategory())
                    .user(party.getUser())
                    .boardType(BoardType.valueOf(boardType))
                    .build();
        } else if (entity instanceof Community community && community.getBoardType() == null) {
            return (T) Community.builder()
                    .id(community.getId())
                    .title(community.getTitle())
                    .content(community.getContent())
                    .category(community.getCategory())
                    .user(community.getUser())
                    .boardType(BoardType.valueOf(boardType))
                    .resolveStatus(community.getResolveStatus())
                    .build();
        } else if (entity instanceof ProjectWithTutor pwt && pwt.getBoardType() == null) {
            return (T) ProjectWithTutor.of(
                    pwt.getTitle(),
                    pwt.getDescription(),
                    pwt.getPrice(),
                    pwt.getDeadline(),
                    pwt.getMaxParticipants(),
                    pwt.getLevel(),
                    pwt.getCategory(),
                    pwt.getUser()
            );
        }
        return entity; // 이미 boardType이 존재하는 경우 그대로 반환
    }
}