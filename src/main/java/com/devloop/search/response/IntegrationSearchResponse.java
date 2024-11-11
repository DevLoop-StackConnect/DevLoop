package com.devloop.search.response;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.lecture.entity.Lecture;
import com.devloop.party.entity.Party;
import com.devloop.pwt.entity.ProjectWithTutor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IntegrationSearchResponse {
    private Long id;
    private String boardType;
    private String title;
    private String content;
    private String category;
    private String username;
    private LocalDateTime createdAt;

    // 기본 생성자 추가
    public IntegrationSearchResponse() {
    }

    // 모든 필드를 받는 생성자
    public IntegrationSearchResponse(Long id, String boardType, String title, String content, String category, String username, LocalDateTime createdAt) {
        this.id = id;
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.category = category;
        this.username = username;
        this.createdAt = createdAt;
    }


    public static IntegrationSearchResponse of(Object data, String boardType) {
        if (data instanceof Community community) {
            return IntegrationSearchResponse.builder()
                    .id(community.getId())
                    .boardType(boardType)
                    .title(community.getTitle())
                    .content(community.getContent())
                    .category(String.valueOf(community.getCategory()))
                    .username(community.getUser().getUsername())
                    .createdAt(community.getCreatedAt())
                    .build();
        } else if (data instanceof Party party) {
            return IntegrationSearchResponse.builder()
                    .id(party.getId())
                    .boardType(boardType)
                    .title(party.getTitle())
                    .content(party.getContents())
                    .category(String.valueOf(party.getCategory()))
                    .username(party.getUser().getUsername())
                    .createdAt(party.getCreatedAt())
                    .build();
        } else if (data instanceof ProjectWithTutor pwt) {
            return IntegrationSearchResponse.builder()
                    .id(pwt.getId())
                    .boardType(boardType)
                    .title(pwt.getTitle())
                    .content(pwt.getDescription())
//                    .category(String.valueOf(pwt.get))
                    .username(pwt.getUser().getUsername())
                    .createdAt(pwt.getCreatedAt())
                    .build();
        } else if (data instanceof Lecture lecture) {
            return IntegrationSearchResponse.builder()
                    .id(lecture.getId())
                    .boardType(boardType)
                    .title(lecture.getTitle())
                    .content(lecture.getDescription())
                    .category(String.valueOf(lecture.getCategory()))
                    .username(lecture.getUser().getUsername())
                    .createdAt(lecture.getCreatedAt())
                    .build();
        }

        throw new ApiException(ErrorStatus._UNSUPPORTED_DATA_TYPE);
    }
}