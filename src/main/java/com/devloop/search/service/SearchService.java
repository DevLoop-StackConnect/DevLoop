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
import com.devloop.search.response.IntegrationSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<IntegrationSearchResponse> integrationSearch(IntegrationSearchRequest integrationSearchRequest, int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        if (integrationSearchRequest.getBoardType() == null || integrationSearchRequest.getBoardType().isEmpty()) {
            log.info("boardType이 null이거나 비어있음");
            return searchAllType(integrationSearchRequest, pageable);
        }
        log.info("boardType: {}",integrationSearchRequest.getBoardType());  // 로그 추가
        log.info("toLowerCase: {}", integrationSearchRequest.getBoardType().toLowerCase());

        log.info("boardType 값: '{}'", integrationSearchRequest.getBoardType());
        String type = integrationSearchRequest.getBoardType().toLowerCase();
        log.info("변환된 boardType 값: '{}'", type);
        log.info("문자열 길이: {}", type.length());
        log.info("각 문자의 ASCII 값: ");
        try {
            return switch(type) {
                case "party" -> {yield searchParty(integrationSearchRequest, pageable);}

                case "community" -> {
                    log.info("community 검색 시작");
                    yield searchCommunity(integrationSearchRequest, pageable);}
                case "project" -> {yield searchPwt(integrationSearchRequest, pageable);}

                default -> {log.error("일치하는 boardType이 없음: '{}'", type);
                    throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
                }
            };

            }catch (Exception e){
            log.error(" 검색 중 에러 발생");
            log.error("에러 메시지: {}", e.getMessage());
            log.error("에러 원인: {}", e.getCause());
            throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        }
    }

    private Page<IntegrationSearchResponse> searchAllType(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {
        List<IntegrationSearchResponse> allResults = new ArrayList<>();

        Specification<Party> partySpec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
        Specification<Community> communitySpec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
        Specification<ProjectWithTutor> pwtSpec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);

        List<IntegrationSearchResponse> partyResults = partyService.getParty(partySpec);

        List<IntegrationSearchResponse> communityResults = communityService.getAllCommunity(communitySpec);

        List<IntegrationSearchResponse> pwtResults = projectWithTutorService.getProjectWithTutor(pwtSpec);

        allResults.addAll(partyResults);
        allResults.addAll(communityResults);
        allResults.addAll(pwtResults);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allResults.size());

        return new PageImpl<>(
                allResults.subList(start, end),
                pageable,
                allResults.size()
        );
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
