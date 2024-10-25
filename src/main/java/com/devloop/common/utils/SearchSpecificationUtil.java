package com.devloop.common.utils;

import com.devloop.common.BoardType;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.search.request.IntegrationSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SearchSpecificationUtil {
    /**
     * 동적 쿼리 생성 util 클래스
     * 검색에 제목만 입력한다든지, 제목 + 작성자를 검색한다던지 할 때 동적 쿼리 사용
     */
    public static <T> Specification<T> buildSpecification(IntegrationSearchRequest request) {
        List<Specification<T>> specs = new ArrayList<>();

        if (request.getBoardType() != null) {
            BoardType boardType = BoardType.valueOf(request.getBoardType().toUpperCase());
            /**
             * root = 쿼리의 루트 엔티티 ex) Party엔티티 기준 party 엔티티의 boardType에 접근한다는 뜻
             * query = 쿼리 객체를 말해영
             * criteriaBuilder = SQL 연산자
             * criteriaBuilder.equal --> SQL 쿼리문에서 = 을 뜻합니당
             * 결론 : 아래 로직은 결국 SQL 쿼리문에서 WHERE board_type = "boardType" 이 쿼리문과 동일
             */
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("boardType"), boardType));
        }

        //제목 검색 동적 쿼리
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
        }

        if (request.getUsename() != null && !request.getUsename().isEmpty()) {
            specs.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("username"), "%" + request.getUsename() + "%"));
        }
        return specs.stream()
                .reduce(Specification::and)
                .orElseThrow(() -> new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD));
    }
}
