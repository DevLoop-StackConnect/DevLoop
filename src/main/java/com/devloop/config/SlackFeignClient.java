package com.devloop.config;

import com.devloop.notification.dto.SlackMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
//Feign클라이언트를 사용하여 Slack Api와 통신하는 인터페이스 (신기함 자동으로 날라간대요)
@FeignClient(name = "slack", url = "${slack.app.base-url}")
public interface SlackFeignClient {
    //Slack의 메시지 전송 Api 엔트포인트와 매핑
    @PostMapping("/chat-postMessage")
    void sendMessage(@RequestBody SlackMessage slackMessage);
    //Slack의 사용자 정보 조회 Api 엔드포인트와 매핑
    @GetMapping("/users-info")
    Object getUserInfo(@RequestParam("user") String userId);
    //Slack의 이메일로 사용자 정보 조회 Api 엔드포인트와 매핑
    @GetMapping("/users-lookupByEmail")
    Object getUserByEmail(@RequestParam("email") String email);
}