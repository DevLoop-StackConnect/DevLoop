package com.devloop.user.controller;

import com.devloop.common.utils.S3Util;
import com.devloop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final S3Util s3Util;

    @PostMapping("/s3test")
    public String s3test(@RequestParam("file") MultipartFile file ) {
        System.out.println("들어옴");
        s3Util.uploadFile(file, "devloop-stackconnect1");
        return "성공";
    }
}
