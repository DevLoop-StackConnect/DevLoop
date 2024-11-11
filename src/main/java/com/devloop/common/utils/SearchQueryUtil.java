package com.devloop.common.utils;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.lecture.entity.Lecture;
import com.devloop.party.entity.Party;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.devloop.search.request.IntegrationSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.devloop.community.entity.QCommunity;
import com.devloop.lecture.entity.QLecture;
import com.devloop.party.entity.QParty;
import com.devloop.pwt.entity.QProjectWithTutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class SearchQueryUtil {

    public static BooleanBuilder buildSearchCondition(IntegrationSearchRequest request, Class<?> entityClass) {
        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder searchBuilder = new BooleanBuilder();

        // Title 조건 처리
        if (StringUtils.hasText(request.getTitle())) {
            BooleanExpression titleCondition = buildTitleCondition(request.getTitle(), entityClass);
            log.debug("Title condition for {}: {}", entityClass.getSimpleName(), titleCondition);
            if (titleCondition != null) {
                searchBuilder.or(titleCondition);
            }
        }

        // Content 조건 처리
        if (StringUtils.hasText(request.getContent())) {
            BooleanExpression contentCondition = buildContentCondition(request.getContent(), entityClass);
            log.debug("Content condition for {}: {}", entityClass.getSimpleName(), contentCondition);
            if (contentCondition != null) {
                searchBuilder.or(contentCondition);
            }
        }

        // Username 조건 처리
        if (StringUtils.hasText(request.getUsername())) {
            BooleanExpression usernameCondition = buildUsernameCondition(request.getUsername(), entityClass);
            log.debug("Username condition for {}: {}", entityClass.getSimpleName(), usernameCondition);
            if (usernameCondition != null) {
                searchBuilder.or(usernameCondition);
            }
        }

        // Category 조건 처리
        if (StringUtils.hasText(request.getCategory())) {
            try {
                Category category = Category.of(request.getCategory());
                BooleanExpression categoryCondition = buildCategoryCondition(category, entityClass);
                if (categoryCondition != null) {
                    builder.and(categoryCondition);
                    log.debug("Category condition for {}: {}", entityClass.getSimpleName(), categoryCondition);
                }
            } catch (Exception e) {
                log.error("Error parsing category: {}", e.getMessage());
                throw new ApiException(ErrorStatus._BAD_SEARCH_KEYWORD);
            }
        }

        // BoardType 조건 처리
        if (StringUtils.hasText(request.getBoardType())) {
            BooleanExpression boardTypeCondition = buildBoardTypeCondition(request.getBoardType(), entityClass);
            if (boardTypeCondition != null) {
                builder.and(boardTypeCondition);
                log.debug("BoardType condition for {}: {}", entityClass.getSimpleName(), boardTypeCondition);
            }
        }

        // 검색 조건 추가 확인
        if (searchBuilder.hasValue()) {
            builder.and(searchBuilder);
            log.debug("Search conditions combined for {}: {}", entityClass.getSimpleName(), builder);
        }

        // 기본 조건 추가 (검색 조건이 없을 때)
        if (builder.getValue() == null) {
            builder = buildDefaultCondition(entityClass, builder);
            log.debug("Default condition applied for {}: {}", entityClass.getSimpleName(), builder);
        }

        log.debug("Final generated conditions for {}: {}", entityClass.getSimpleName(), builder);
        return builder;
    }

    private static BooleanExpression buildTitleCondition(String title, Class<?> entityClass) {
        log.debug("Building title condition for entity: {} with title: {}", entityClass.getSimpleName(), title);

        if (entityClass.equals(Community.class)) {
            log.debug("Applying title condition for Community");
            return QCommunity.community.title.containsIgnoreCase(title);
        } else if (entityClass.equals(Party.class)) {
            log.debug("Applying title condition for Party");
            return QParty.party.title.containsIgnoreCase(title);
        } else if (entityClass.equals(Lecture.class)) {
            log.debug("Applying title condition for Lecture");
            return QLecture.lecture.title.containsIgnoreCase(title);
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            log.debug("Applying title condition for ProjectWithTutor");
            return QProjectWithTutor.projectWithTutor.title.containsIgnoreCase(title);
        }

        log.debug("No matching entity class found for title condition, returning null");
        return null;
    }

    private static BooleanExpression buildContentCondition(String content, Class<?> entityClass) {
        if (entityClass.equals(Community.class)) {
            return QCommunity.community.content.containsIgnoreCase(content);
        } else if (entityClass.equals(Party.class)) {
            return QParty.party.contents.containsIgnoreCase(content);
        } else if (entityClass.equals(Lecture.class)) {
            return QLecture.lecture.description.containsIgnoreCase(content);
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.description.containsIgnoreCase(content);
        }
        return null;
    }

    private static BooleanExpression buildUsernameCondition(String username, Class<?> entityClass) {
        if (entityClass.equals(Community.class)) {
            return QCommunity.community.user.username.containsIgnoreCase(username);
        } else if (entityClass.equals(Party.class)) {
            return QParty.party.user.username.containsIgnoreCase(username);
        } else if (entityClass.equals(Lecture.class)) {
            return QLecture.lecture.user.username.containsIgnoreCase(username);
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.user.username.containsIgnoreCase(username);
        }
        return null;
    }

    private static BooleanExpression buildCategoryCondition(Category category, Class<?> entityClass) {
        if (entityClass.equals(Community.class)) {
            return QCommunity.community.category.eq(category);
        } else if (entityClass.equals(Party.class)) {
            return QParty.party.category.eq(category);
        } else if (entityClass.equals(Lecture.class)) {
            return QLecture.lecture.category.eq(category);
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.category.eq(category);
        }
        return null;
    }

    private static BooleanExpression buildBoardTypeCondition(String boardType, Class<?> entityClass) {
        if (entityClass.equals(Party.class)) {
            return QParty.party.boardType.stringValue().equalsIgnoreCase(boardType);
        } else if (entityClass.equals(Community.class)) {
            return QCommunity.community.boardType.stringValue().equalsIgnoreCase(boardType);
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.boardType.stringValue().equalsIgnoreCase(boardType);
        }
        return null;
    }

    private static BooleanBuilder buildDefaultCondition(Class<?> entityClass, BooleanBuilder builder) {
        if (entityClass.equals(Party.class)) {
            return builder.and(QParty.party.id.isNull());
        } else if (entityClass.equals(Community.class)) {
            return builder.and(QCommunity.community.id.isNull());
        } else if (entityClass.equals(Lecture.class)) {
            return builder.and(QLecture.lecture.id.isNull());
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            return builder.and(QProjectWithTutor.projectWithTutor.id.isNull());
        }
        return builder;
    }
}