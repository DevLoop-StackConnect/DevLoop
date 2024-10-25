package com.devloop.common.utils;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.search.request.IntegrationSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SearchSpecificationUtil {

    public static <T> Specification<T> buildSpecification(IntegrationSearchRequest request) {
        List<Specification<T>> specs = new ArrayList<>();

        if ((request.getTitle() == null || request.getTitle().isEmpty()) &&
                (request.getUsername() == null || request.getUsername().isEmpty()) &&
                (request.getCategory() == null || request.getCategory().isEmpty())) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("id"));
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("user").get("username"), "%" + request.getUsername() + "%"));
        }

        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            try {
                Category category = Category.of(request.getCategory());
                specs.add((root, query, criteriaBuilder) -> {
                    return criteriaBuilder.equal(root.get("category"), category);
                });
            } catch (Exception e) {
                throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
            }
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElseThrow(() -> new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD));
    }
}
