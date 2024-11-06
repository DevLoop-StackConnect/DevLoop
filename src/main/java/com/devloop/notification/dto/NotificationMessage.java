package com.devloop.notification.dto;

import com.devloop.notification.enums.NotificationType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
//Slack 등의 알림 메시지를 표현하는 DTO
public class NotificationMessage {
    //알림 유형
    private NotificationType type;
    //알림 전송 대상(채널)
    private String notificationTarget;
    //알림 관련 data
    private Map<String, Object> data;

    //Json 역직렬화
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //Json 직렬화
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime timestamp;
}
