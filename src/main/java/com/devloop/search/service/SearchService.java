package com.devloop.search.service;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.common.utils.SearchSpecificationUtil;
import com.devloop.community.entity.Community;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.community.service.CommunityService;
import com.devloop.party.entity.Party;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.service.PartyService;
import com.devloop.search.request.IntegrationSearchRequest;
import com.devloop.search.response.IntegrationSearchResponse;
import com.devloop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PartyService partyService;
final UserRepository userRepository;
    private final CommunityService communityService;

    public Page<IntegrationSearchResponse> integrationSearch(IntegrationSearchRequest integrationSearchRequest, int page, int size) {
        PageRequest pageable= PageRequest.of(page-1,size, Sort.by("createdAt").descending());

        if(integrationSearchRequest.getBoardType() == null || integrationSearchRequest.getBoardType().isEmpty()) {
            return searchAllType(integrationSearchRequest, pageable);
        }
        return switch(integrationSearchRequest.getBoardType().toLowerCase()){
            case "party" -> searchParty(integrationSearchRequest, pageable);
            case "community" -> searchCommunity(integrationSearchRequest, pageable);
//            case "project"  -> searchProject(integrationSearchRequest, pageable);
            default -> throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
        };
    }

    private Page<IntegrationSearchResponse> searchAllType(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {
        List<IntegrationSearchResponse> allResults = new ArrayList<>();

            Specification<Party> partySpec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
            Specification<Community> communitySpec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
//        Specification<프로젝트> projectSpec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);

            List<IntegrationSearchResponse> partyResults = partyService.getParty(partySpec);

            List<IntegrationSearchResponse> communityResults = communityService.getCommunity(communitySpec)
                    .stream()
                    .map(community -> IntegrationSearchResponse.of("community", community))
                    .toList();
//
//        List<IntegrationSearchResponse> projectResults = partyRepository.findAll(partySpec)
//                .stream()
//                .map(project -> IntegrationSearchResponse.from("project",project))
//                .toList();

            allResults.addAll(partyResults);
            allResults.addAll(communityResults);
//        allResults.addAll(projectResults);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allResults.size());

        return new PageImpl<>(
                allResults.subList(start,end),
                pageable,
                allResults.size()
        );
    }

    private Page<IntegrationSearchResponse> searchParty(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {

        Specification<Party> spec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
        return partyService.getPartyWithPage(spec, pageable);
    }

    private Page<IntegrationSearchResponse> searchCommunity(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {

        Specification<Community> spec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
        return communityService.getCommunityWithPage(spec, pageable);
    }

//    private Page<IntegrationSearchResponse> searchProject(IntegrationSearchRequest integrationSearchRequest, PageRequest pageable) {
//
//        Specification<Party> spec = SearchSpecificationUtil.buildSpecification(integrationSearchRequest);
//        return partyRepository.findAll(spec, pageable)
//                .map(party -> IntegrationSearchResponse.from("project", project));
//    }
}
