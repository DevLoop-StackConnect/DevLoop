package com.devloop.lecture.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MultipartUploadCompleteRequest {
    @JsonProperty("uploadId")
    private String uploadId;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("parts")
    private List<UploadCompletePart> parts;
}
