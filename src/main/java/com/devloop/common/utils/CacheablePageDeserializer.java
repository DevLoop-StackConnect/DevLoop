package com.devloop.common.utils;

import com.devloop.search.response.IntegrationSearchResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheablePageDeserializer extends JsonDeserializer<CacheablePage<IntegrationSearchResponse>> {
    @Override
    public CacheablePage<IntegrationSearchResponse> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        JsonNode node = mapper.readTree(p);

        List<IntegrationSearchResponse> content = new ArrayList<>();
        JsonNode contentNode = node.get("content");
        if (contentNode != null && contentNode.get(1) != null && contentNode.get(1).isArray()) {
            ArrayNode arrayNode = (ArrayNode) contentNode.get(1);
            for (JsonNode item : arrayNode) {
                try {
                    IntegrationSearchResponse response = mapper.treeToValue(item, IntegrationSearchResponse.class);
                    content.add(response);
                } catch (Exception e) {
                    // Skip invalid items
                    continue;
                }
            }
        }

        return CacheablePage.<IntegrationSearchResponse>builder()
                .content(content)
                .totalElements(node.get("totalElements").asLong())
                .pageNumber(node.get("pageNumber").asInt())
                .pageSize(node.get("pageSize").asInt())
                .sortDirection(node.get("sortDirection").asText())
                .sortProperty(node.get("sortProperty").asText())
                .build();
    }
}