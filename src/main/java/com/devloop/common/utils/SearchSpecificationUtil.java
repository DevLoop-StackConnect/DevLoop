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

        // BoardType 검사를 먼저 수행 - 엔티티별로 다르게 처리
        if (request.getBoardType() != null && !request.getBoardType().isEmpty()) {
            specs.add((root, query, criteriaBuilder) -> {
                if (root.getModel().getJavaType().getSimpleName().equals("Lecture")) {
                    return criteriaBuilder.equal(criteriaBuilder.literal(true),
                            request.getBoardType().toLowerCase().equals("lecture"));
                } else {
                    return criteriaBuilder.equal(root.get("boardType"), request.getBoardType().toLowerCase());
                }
            });

            // BoardType만 있고 다른 검색 조건이 없는 경우
            if ((request.getTitle() == null || request.getTitle().isEmpty()) &&
                    (request.getUsername() == null || request.getUsername().isEmpty()) &&
                    (request.getCategory() == null || request.getCategory().isEmpty()) &&
                    (request.getLecture() == null || request.getLecture().isEmpty())) {
                return specs.get(0);
            }
        }

        // Lecture 검색
        if (request.getLecture() != null && !request.getLecture().isEmpty()) {
            return (root, query, criteriaBuilder) -> {
                if (root.getModel().getJavaType().getSimpleName().equals("Lecture")) {
                    String searchKeyword = "%" + request.getLecture().toLowerCase() + "%";
                    return criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchKeyword),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchKeyword)
                    );
                }
                return criteriaBuilder.isNull(root.get("id"));
            };
        }

        // Title 검색
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                            "%" + request.getTitle().toLowerCase() + "%"));
        }

        // Username 검색
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")),
                            "%" + request.getUsername().toLowerCase() + "%"));
        }

        // Category 검색
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            try {
                Category category = Category.of(request.getCategory());
                specs.add((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("category"), category));
            } catch (Exception e) {
                throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
            }
        }

        // 검색 조건이 없는 경우
        if (specs.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("id"));
        }

        // 모든 조건을 AND로 결합
        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("id")));
    }
}