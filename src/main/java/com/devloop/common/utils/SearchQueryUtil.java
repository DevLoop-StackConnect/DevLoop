package com.devloop.common.utils;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.search.request.IntegrationSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.devloop.community.entity.QCommunity;
import com.devloop.lecture.entity.QLecture;
import com.devloop.party.entity.QParty;
import com.devloop.pwt.entity.QProjectWithTutor;
import org.springframework.util.StringUtils;

public class SearchQueryUtil {

    public static BooleanBuilder buildSearchCondition(IntegrationSearchRequest request, Class<?> entityClass) {
        BooleanBuilder builder = new BooleanBuilder();

        // BoardType 조건
        if (StringUtils.hasText(request.getBoardType()) && !entityClass.equals(QLecture.class)) {
            builder.and(boardTypeEq(request.getBoardType(), entityClass));
        }

        // Content 조건
        if (StringUtils.hasText(request.getContent())) {
            builder.and(contentEq(request.getContent(), entityClass));
        }

        // Lecture 조건
        if (StringUtils.hasText(request.getLecture())) {
            builder.and(lectureEq(request.getLecture(), entityClass));
        }

        // Title 조건
        if (StringUtils.hasText(request.getTitle())) {
            builder.and(titleEq(request.getTitle(), entityClass));
        }

        // Username 조건
        if (StringUtils.hasText(request.getUsername())) {
            builder.and(usernameEq(request.getUsername(), entityClass));
        }

        // Category 조건
        if (StringUtils.hasText(request.getCategory())) {
            try {
                Category category = Category.of(request.getCategory());
                builder.and(categoryEq(category, entityClass));
            } catch (Exception e) {
                throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
            }
        }

        // 검색 조건이 없는 경우, 아무 조건도 없는 상태로 반환
        if (builder.getValue() == null) {
            return new BooleanBuilder().andAnyOf(QCommunity.community.id.isNull());
        }

        return builder;
    }

    // BoardType 조건 생성
    private static BooleanExpression boardTypeEq(String boardType, Class<?> entityClass) {
        if (entityClass.equals(QParty.class)) {
            return QParty.party.boardType.stringValue().equalsIgnoreCase(boardType);
        }
        return null;
    }

    // Content 조건 생성
    private static BooleanExpression contentEq(String content, Class<?> entityClass) {
        if (entityClass.equals(QCommunity.class)) {
            return QCommunity.community.content.containsIgnoreCase(content);
        } else if (entityClass.equals(QParty.class)) {
            return QParty.party.contents.containsIgnoreCase(content);
        } else if (entityClass.equals(QLecture.class)) {
            return QLecture.lecture.description.containsIgnoreCase(content);
        } else if (entityClass.equals(QProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.description.containsIgnoreCase(content);
        }
        return null;
    }

    // Lecture 조건 생성
    private static BooleanExpression lectureEq(String lecture, Class<?> entityClass) {
        if (entityClass.equals(QLecture.class)) {
            return QLecture.lecture.title.containsIgnoreCase(lecture)
                    .or(QLecture.lecture.description.containsIgnoreCase(lecture));
        } else if (entityClass.equals(QParty.class) || entityClass.equals(QCommunity.class)) {
            return QParty.party.title.containsIgnoreCase(lecture);
        } else if (entityClass.equals(QProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.description.containsIgnoreCase(lecture);
        }
        return null;
    }

    // Title 조건 생성
    private static BooleanExpression titleEq(String title, Class<?> entityClass) {
        if (entityClass.equals(QCommunity.class)) {
            return QCommunity.community.title.containsIgnoreCase(title);
        } else if (entityClass.equals(QParty.class)) {
            return QParty.party.title.containsIgnoreCase(title);
        } else if (entityClass.equals(QLecture.class)) {
            return QLecture.lecture.title.containsIgnoreCase(title);
        } else if (entityClass.equals(QProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.title.containsIgnoreCase(title);
        }
        return null;
    }

    // Username 조건 생성
    private static BooleanExpression usernameEq(String username, Class<?> entityClass) {
        if (entityClass.equals(QCommunity.class)) {
            return QCommunity.community.user.username.containsIgnoreCase(username);
        } else if (entityClass.equals(QParty.class)) {
            return QParty.party.user.username.containsIgnoreCase(username);
        } else if (entityClass.equals(QLecture.class)) {
            return QLecture.lecture.user.username.containsIgnoreCase(username);
        } else if (entityClass.equals(QProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.user.username.containsIgnoreCase(username);
        }
        return null;
    }

    // Category 조건 생성
    private static BooleanExpression categoryEq(Category category, Class<?> entityClass) {
        if (entityClass.equals(QCommunity.class)) {
            return QCommunity.community.category.eq(category);
        } else if (entityClass.equals(QParty.class)) {
            return QParty.party.category.eq(category);
        } else if (entityClass.equals(QLecture.class)) {
            return QLecture.lecture.category.eq(category);
        } else if (entityClass.equals(QProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.category.eq(category);
        }
        return null;
    }
}