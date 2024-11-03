package com.devloop.search.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.BoardType;
import com.devloop.common.exception.ApiException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PartyService partyService;
    private final CommunityService communityService;
    private final ProjectWithTutorService projectWithTutorService;

    private static final int PREVIEW_SIZE = 5;

    @Cacheable(
            value = "searchPreview",
            key = "T(com.devloop.common.utils.CacheKeyGenerator).generateKey(#request)",
            condition = "#request != null",
            unless = "#result == null || #result.isEmpty()"
    )
    public IntegratedSearchPreview integratedSearchPreview(IntegrationSearchRequest request) {
        log.debug("Cache miss for search preview: {}", request);

        try {
            Specification<Party> partySpec = SearchSpecificationUtil.buildSpecification(request);
            Specification<Community> communitySpec = SearchSpecificationUtil.buildSpecification(request);
            Specification<ProjectWithTutor> pwtSpec = SearchSpecificationUtil.buildSpecification(request);

            Page<IntegrationSearchResponse> partyResults = partyService.getPartyWithPage(
                    partySpec, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

            Page<IntegrationSearchResponse> communityResults = communityService.getCommunityWithPage(
                    communitySpec, PageRequest.of(0, PREVIEW_SIZE, Sort.by("createdAt").descending()));

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
        } catch (Exception e) {
            log.error("Error during search preview", e);
            return IntegratedSearchPreview.builder().build();
        }
    }

    @Cacheable(
            value = "searchDetail",
            key = "T(com.devloop.common.utils.CacheKeyGenerator).generateCategoryKey(#category, #page, #request)",
            condition = "#request != null && #page > 0",
            unless = "#result == null || #result.isEmpty()"
    )
    public Page<IntegrationSearchResponse> searchByCategory(
            IntegrationSearchRequest request, String category, int page, int size) {
        log.debug("Cache miss for category search: {} - page: {}", category, page);

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return switch (category.toLowerCase()) {
            case "party" -> searchParty(request, pageable);
            case "community" -> searchCommunity(request, pageable);
            case "pwt" -> searchPwt(request, pageable);
            default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        };
    }

    @CacheEvict(value = {"searchPreview", "searchDetail"}, allEntries = true)
    public void clearSearchCache() {
        log.info("Clearing all search caches");
    }

    private Page<IntegrationSearchResponse> searchParty(IntegrationSearchRequest request, PageRequest pageable) {
        Specification<Party> spec = SearchSpecificationUtil.buildSpecification(request);
        return partyService.getPartyWithPage(spec, pageable);
    }

    private Page<IntegrationSearchResponse> searchCommunity(IntegrationSearchRequest request, PageRequest pageable) {
        Specification<Community> spec = SearchSpecificationUtil.buildSpecification(request);
        return communityService.getCommunityWithPage(spec, pageable);
    }

    private Page<IntegrationSearchResponse> searchPwt(IntegrationSearchRequest request, PageRequest pageable) {
        Specification<ProjectWithTutor> spec = SearchSpecificationUtil.buildSpecification(request);
        return projectWithTutorService.getProjectWithTutorPage(spec, pageable);
    }
}