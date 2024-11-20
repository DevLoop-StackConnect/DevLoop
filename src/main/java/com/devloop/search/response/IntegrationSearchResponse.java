package com.devloop.search.response;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import com.devloop.community.entity.Community;
import com.devloop.lecture.entity.Lecture;
import com.devloop.party.entity.Party;
import com.devloop.pwt.entity.ProjectWithTutor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class",
        visible = true
)
@JsonDeserialize(using = IntegrationSearchResponse.IntegrationSearchResponseDeserializer.class)

public class IntegrationSearchResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty
    private Long id;
    @JsonProperty
    private String boardType;
    @JsonProperty
    private String title;
    @JsonProperty
    private String content;
    @JsonProperty
    private String category;
    @JsonProperty
    private String username;

    @JsonProperty
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonProperty
    @JsonDeserialize(using = CustomFloatDeserializer.class)
    private float score;

    public static class CustomFloatDeserializer extends JsonDeserializer<Float> {
        @Override
        public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            if ("NaN".equalsIgnoreCase(value) || value == null || value.isEmpty()) {
                return 0.0f;
            }
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
    }

    public static IntegrationSearchResponse of(Object data, float score) {
        String boardType;
        IntegrationSearchResponse response;

        if (data instanceof Community community) {
            boardType = "community";
            response = IntegrationSearchResponse.builder()
                    .id(community.getId())
                    .boardType(boardType)
                    .title(community.getTitle())
                    .content(community.getContent())
                    .category(String.valueOf(community.getCategory()))
                    .username(community.getUser().getUsername())
                    .createdAt(community.getCreatedAt())
                    .score(score)
                    .build();
        } else if (data instanceof Party party) {
            boardType = "party";
            response = IntegrationSearchResponse.builder()
                    .id(party.getId())
                    .boardType(boardType)
                    .title(party.getTitle())
                    .content(party.getContents())
                    .category(String.valueOf(party.getCategory()))
                    .username(party.getUser().getUsername())
                    .createdAt(party.getCreatedAt())
                    .score(score)
                    .build();
        } else if (data instanceof ProjectWithTutor pwt) {
            boardType = "pwt";
            response = IntegrationSearchResponse.builder()
                    .id(pwt.getId())
                    .boardType(boardType)
                    .title(pwt.getTitle())
                    .content(pwt.getDescription())
                    .category(String.valueOf(pwt.getCategory()))
                    .username(pwt.getUser().getUsername())
                    .createdAt(pwt.getCreatedAt())
                    .score(score)
                    .build();
        } else if (data instanceof Lecture lecture) {
            boardType = "lecture";
            response = IntegrationSearchResponse.builder()
                    .id(lecture.getId())
                    .boardType(boardType)
                    .title(lecture.getTitle())
                    .content(lecture.getDescription())
                    .category(String.valueOf(lecture.getCategory()))
                    .username(lecture.getUser().getUsername())
                    .createdAt(lecture.getCreatedAt())
                    .score(score)
                    .build();
        } else {
            throw new ApiException(ErrorStatus._UNSUPPORTED_DATA_TYPE);
        }
        return response;
    }

    public static class IntegrationSearchResponseDeserializer extends JsonDeserializer<IntegrationSearchResponse> {
        @Override
        public IntegrationSearchResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            JsonNode node = mapper.readTree(p);

            Long id = null;
            JsonNode idNode = node.get("id");
            if (idNode != null && idNode.isArray() && idNode.size() > 1) {
                id = idNode.get(1).asLong();
            }

            LocalDateTime createdAt = null;
            JsonNode createdAtNode = node.get("createdAt");
            if (createdAtNode != null && createdAtNode.isArray() && createdAtNode.size() > 1) {
                createdAt = LocalDateTime.parse(createdAtNode.get(1).asText());
            }

            return IntegrationSearchResponse.builder()
                    .id(id)
                    .boardType(getTextSafely(node, "boardType"))
                    .title(getTextSafely(node, "title"))
                    .content(getTextSafely(node, "content"))
                    .category(getTextSafely(node, "category"))
                    .username(getTextSafely(node, "username"))
                    .createdAt(createdAt)
                    .score(parseScore(node.get("score")))
                    .build();
        }

        private String getTextSafely(JsonNode node, String fieldName) {
            JsonNode fieldNode = node.get(fieldName);
            return fieldNode != null ? fieldNode.asText() : null;
        }

        private float parseScore(JsonNode scoreNode) {
            if (scoreNode == null || scoreNode.isNull() ||
                    "NaN".equalsIgnoreCase(scoreNode.asText())) {
                return 0.0f;
            }
            try {
                return Float.parseFloat(scoreNode.asText());
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
    }
}
