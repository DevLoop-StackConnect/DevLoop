package com.devloop.common.utils;

import com.devloop.common.BoardType;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.search.request.IntegrationSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SearchSpecificationUtil {

    public static <T> Specification<T> buildSpecification(IntegrationSearchRequest request) {
        List<Specification<T>> specs = new ArrayList<>();

        //제목 검색 동적 쿼리
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("user").get("username"), "%" + request.getUsername() + "%"));
        }
        return specs.stream()
                .reduce(Specification::and)
                .orElseThrow(() -> new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD));
    }
}
