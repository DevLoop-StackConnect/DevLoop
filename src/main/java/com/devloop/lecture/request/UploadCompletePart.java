package com.devloop.lecture.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UploadCompletePart {
    @JsonProperty("partNumber")
    private int partNumber;
    @JsonProperty("eTag")
    private String eTag;
}
