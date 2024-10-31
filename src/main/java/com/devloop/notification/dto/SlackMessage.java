package com.devloop.notification.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Block;

import java.util.List;

@Data
@Builder
public class SlackMessage {
    private String text;
    private String channel;
}