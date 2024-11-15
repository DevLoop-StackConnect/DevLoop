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

    public static IntegrationSearchResponse of(Object data, String boardType) {
        IntegrationSearchResponse response;
        if (data instanceof Community community) {
            response = IntegrationSearchResponse.builder()
                    .id(community.getId())
                    .boardType(boardType)
                    .title(community.getTitle())
                    .content(community.getContent())
                    .category(String.valueOf(community.getCategory()))
                    .username(community.getUser().getUsername())
                    .createdAt(community.getCreatedAt())
                    .build();
        } else if (data instanceof Party party) {
            response = IntegrationSearchResponse.builder()
                    .id(party.getId())
                    .boardType(boardType)
                    .title(party.getTitle())
                    .content(party.getContents())
                    .category(String.valueOf(party.getCategory()))
                    .username(party.getUser().getUsername())
                    .createdAt(party.getCreatedAt())
                    .build();
        } else if (data instanceof ProjectWithTutor pwt) {
            response = IntegrationSearchResponse.builder()
                    .id(pwt.getId())
                    .boardType(boardType)
                    .title(pwt.getTitle())
                    .content(pwt.getDescription())
                    .category(String.valueOf(pwt.getCategory()))
                    .username(pwt.getUser().getUsername())
                    .createdAt(pwt.getCreatedAt())
                    .build();
        } else if (data instanceof Lecture lecture) {
            response = IntegrationSearchResponse.builder()
                    .id(lecture.getId())
                    .boardType(boardType)
                    .title(lecture.getTitle())
                    .content(lecture.getDescription())
                    .category(String.valueOf(lecture.getCategory()))
                    .username(lecture.getUser().getUsername())
                    .createdAt(lecture.getCreatedAt())
                    .build();
        } else {
            throw new ApiException(ErrorStatus._UNSUPPORTED_DATA_TYPE);
        }

        // 디버그 로그 추가
        System.out.println("Converted response: " + response);
        return response;
    }
}