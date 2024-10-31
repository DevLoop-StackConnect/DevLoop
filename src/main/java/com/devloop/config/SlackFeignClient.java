package com.devloop.config;

import com.devloop.notification.dto.SlackMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "slack", url = "${slack.app.base-url}")
public interface SlackFeignClient {
    @PostMapping("/chat-postMessage")
    void sendMessage(@RequestBody SlackMessage slackMessage);

    @GetMapping("/users-info")
    Object getUserInfo(@RequestParam("user") String userId);

    @GetMapping("/users-lookupByEmail")
    Object getUserByEmail(@RequestParam("email") String email);
}