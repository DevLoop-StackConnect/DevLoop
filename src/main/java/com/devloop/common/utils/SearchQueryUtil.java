package com.devloop.common.utils;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Approval;
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

        // Lecture 조건 처리
        if (StringUtils.hasText(request.getLecture())) {
            BooleanExpression lectureCondition = buildLectureCondition(request.getLecture(), entityClass);
            if (lectureCondition != null) {
                builder.and(lectureCondition);
                log.debug("lecture condition for {}: {}", entityClass.getSimpleName(), lectureCondition);
            }
        }

        // 검색 조건 추가 확인
        if (searchBuilder.hasValue()) {
            builder.and(searchBuilder);
            log.debug("Search conditions combined for {}: {}", entityClass.getSimpleName(), builder);
        }

        log.debug("Final generated conditions for {}: {}", entityClass.getSimpleName(), builder);
        return builder;
    }
    private static BooleanExpression buildBaseCondition(Class<?> entityClass) {
        if (entityClass.equals(Party.class)) {
            return QParty.party.id.isNotNull();
        } else if (entityClass.equals(Community.class)) {
            return QCommunity.community.id.isNotNull();
        } else if (entityClass.equals(Lecture.class)) {
            // Lecture의 경우 승인된 것만 검색되도록 추가 조건
            return QLecture.lecture.id.isNotNull()
                    .and(QLecture.lecture.approval.eq(Approval.APPROVED));
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            return QProjectWithTutor.projectWithTutor.id.isNotNull();
        }
        return null;
    }

    private static BooleanExpression buildLectureCondition(String lecture, Class<?> entityClass) {
        log.debug("Building title condition for entity: {} with lecture: {}", entityClass.getSimpleName(), lecture);

        if (entityClass.equals(Community.class)) {
            log.debug("Applying lecture condition for Community");
            return QCommunity.community.title.containsIgnoreCase(lecture);
        } else if (entityClass.equals(Party.class)) {
            log.debug("Applying lecture condition for Party");
            return QParty.party.title.containsIgnoreCase(lecture);
        } else if (entityClass.equals(Lecture.class)) {
            log.debug("Applying lecture condition for Lecture");
            return QLecture.lecture.title.containsIgnoreCase(lecture);
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            log.debug("Applying lecture condition for ProjectWithTutor");
            return QProjectWithTutor.projectWithTutor.title.containsIgnoreCase(lecture);
        }

        log.debug("No matching entity class found for lecture condition, returning null");
        return null;
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
        // 각 엔티티에 대해 해당 boardType이 아닌 경우 isNull() 조건으로 결과 제외
        if (entityClass.equals(Party.class)) {
            // party 게시판이 아닌 경우 검색에서 제외
            return !boardType.equalsIgnoreCase("party") ?
                    QParty.party.id.isNull() : null;
        } else if (entityClass.equals(Community.class)) {
            // community 게시판이 아닌 경우 검색에서 제외
            return !boardType.equalsIgnoreCase("community") ?
                    QCommunity.community.id.isNull() : null;
        } else if (entityClass.equals(Lecture.class)) {
            // lecture 게시판이 아닌 경우 검색에서 제외
            return !boardType.equalsIgnoreCase("lecture") ?
                    QLecture.lecture.id.isNull() : null;
        } else if (entityClass.equals(ProjectWithTutor.class)) {
            // pwt 게시판이 아닌 경우 검색에서 제외
            return !boardType.equalsIgnoreCase("pwt") ?
                    QProjectWithTutor.projectWithTutor.id.isNull() : null;
        }
        return null;
    }
}