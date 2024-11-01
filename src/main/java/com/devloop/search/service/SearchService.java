package com.devloop.search.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchResponseUtil;
import com.devloop.common.utils.SearchSpecificationUtil;
import com.devloop.community.entity.Community;
import com.devloop.community.service.CommunityService;
import com.devloop.party.entity.Party;
import com.devloop.party.service.PartyService;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.pwt.service.ProjectWithTutorService;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegratedSearchPreview;
import com.devloop.search.response.IntegrationSearchResponse;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final PartyService partyService;
    private final CommunityService communityService;
    private final ProjectWithTutorService projectWithTutorService;
    private final RedisTemplate<String, Object> redisTemplate;

    //각 카테고리 별 미리보기 게시글 갯수
    private static final int PREVIEW_SIZE = 5;

    @Cacheable(value = "searchPreview", key = "#request.generateCacheKey()", unless = "#result ==null")
    public IntegratedSearchPreview integratedSearchPreview(IntegrationSearchRequest request){
        log.debug("검색 미리보기에서 캐시 미스가 발생하였습니다 : {}", request);

        Specification<Party> partySpec = SearchSpecificationUtil.buildSpecification(request);
        Specification<Community> communitySpec = SearchSpecificationUtil.buildSpecification(request);
        Specification<ProjectWithTutor> pwtSpec = SearchSpecificationUtil.buildSpecification(request);

        Page<IntegrationSearchResponse> partyResults = partyService.getPartyWithPage(
            partySpec, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

        Page<IntegrationSearchResponse> communityResults = communityService.getCommunityWithPage(
                communitySpec, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createAt").descending()));

        Page<IntegrationSearchResponse> pwtResults = projectWithTutorService.getProjectWithTutorPage(
                pwtSpec, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

        return IntegratedSearchPreview.builder()
                .partyPreview(partyResults.getContent())
                .communityPreview(communityResults.getContent())
                .pwtPreview(pwtResults.getContent())
                .totalCommunityCount(communityResults.getTotalElements())
                .totalPartyCount(partyResults.getTotalElements())
                .totalPwtCount(pwtResults.getTotalElements())
                .build();
    }

    public Page<IntegrationSearchResponse> searchByCategory(IntegrationSearchRequest request, String category, int page, int size){
        PageRequest pageable = PageRequest.of(page -1, size, Sort.by("createdAt").descending());

        return switch (category.toLowerCase()){
            case "party" -> searchParty(request, pageable);
            case "community" -> searchCommunity(request, pageable);
            case "pwt" -> searchPwt(request, pageable);
            default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        };
    }

    private Page<IntegrationSearchResponse> searchParty(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {
        Specification<Party> spec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
        Page<IntegrationSearchResponse> result = partyService.getPartyWithPage(spec, pageable);
        return result;
    }

    private Page<IntegrationSearchResponse> searchCommunity(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {
            Specification<Community> spec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
            Page<IntegrationSearchResponse> result = communityService.getCommunityWithPage(spec, pageable);
            return result;
    }

    private Page<IntegrationSearchResponse> searchPwt(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {
        Specification<ProjectWithTutor> spec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
        Page<IntegrationSearchResponse> result = projectWithTutorService.getProjectWithTutorPage(spec, pageable);
        return result;
        //파샤드 패턴
    }
}
