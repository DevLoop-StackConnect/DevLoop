package com.devloop.attachment.cloudfront;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class CloudFrontController {

    private final CloudFrontService cloudFrontService;

    @GetMapping("/cloudfront/signedUrl")
    public String generateSignedUrl(
            @RequestParam String path,
            @RequestParam(defaultValue = "60") int minutes) {
        try {
            return cloudFrontService.generateSignedUrl(path, minutes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating signed URL";
        }
    }
}
