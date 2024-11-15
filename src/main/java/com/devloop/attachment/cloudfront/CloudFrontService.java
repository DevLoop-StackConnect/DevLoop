package com.devloop.attachment.cloudfront;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.Paths;
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

    @Value("${cloud.aws.cloudfront.distributionId}")
    private String DISTRIBUTION_ID;

    private final CloudFrontClient cloudFrontClient;

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

    public String invalidateCache(String path) {
        CreateInvalidationRequest invalidationRequest = CreateInvalidationRequest.builder()
                .distributionId(DISTRIBUTION_ID)
                .invalidationBatch(builder -> builder
                        .paths(Paths.builder()
                                .quantity(1)
                                .items(path)
                                .build())
                        .callerReference(String.valueOf(System.currentTimeMillis())))
                .build();

        CreateInvalidationResponse response = cloudFrontClient.createInvalidation(invalidationRequest);
        return "Invalidation created: " + response.invalidation().id();
    }
}

