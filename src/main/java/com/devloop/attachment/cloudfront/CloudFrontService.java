package com.devloop.attachment.cloudfront;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CloudFrontService {

    @Value("${cloud.aws.cloudfront.cloudFrontUrl}")
    private String CLOUD_FRONT_URL;

    @Value("${cloud.aws.cloudfront.keyPairId}")
    private String KEY_PAIR_ID;

    @Value("${cloud.aws.cloudfront.privateKeyPath}")
    private String PRIVATE_KEY_PATH;

    public String generateSignedUrl(String resourcePath, long expirationMinutes) throws Exception {
        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
        Instant expirationDate = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
        resourcePath = URLEncoder.encode(resourcePath, StandardCharsets.UTF_8);
        CannedSignerRequest request = CannedSignerRequest.builder()
                .resourceUrl(CLOUD_FRONT_URL+resourcePath)
                .privateKey(new java.io.File(PRIVATE_KEY_PATH).toPath())
                .keyPairId(KEY_PAIR_ID)
                .expirationDate(expirationDate)
                .build();

        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);

        return signedUrl.url();
    }
}

