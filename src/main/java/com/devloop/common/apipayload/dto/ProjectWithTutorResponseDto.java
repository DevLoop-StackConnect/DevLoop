package com.devloop.common.apipayload.dto;

import com.devloop.pwt.enums.Level;
import com.devloop.pwt.enums.ProjectWithTutorStatus;
import com.devloop.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class ProjectWithTutorResponseDto {
    private final Long id;
    private final String title;
    private final BigDecimal price;
    private final ProjectWithTutorStatus status;
    private final LocalDateTime deadline;
    private final Integer maxParticipants;
    private final Level level;
    private final User user;

    //pageImpl 생각해보기 (테스트 중 필요 시 생성)
}
