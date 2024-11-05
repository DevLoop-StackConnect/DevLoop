package com.devloop.common.apipayload;

import com.devloop.common.apipayload.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({"success", "statusCode", "message", "data"})
public class ApiResponse<T> {

    @JsonProperty("success")
    private final Boolean success;

    private final String statusCode;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, SuccessStatus._OK.getStatusCode(), SuccessStatus._OK.getMessage(), data);
    }

    // success() 메서드 추가
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, SuccessStatus._OK.getStatusCode(), SuccessStatus._OK.getMessage(), null);
    }

    // Map 등의 데이터를 포함한 success 응답을 위한 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, SuccessStatus._OK.getStatusCode(), SuccessStatus._OK.getMessage(), data);
    }

    public static ApiResponse<String> fail(BaseCode errorCode) {
        return new ApiResponse<>(false, errorCode.getReasonHttpStatus().getStatusCode(), errorCode.getReasonHttpStatus().getMessage(), null);
    }
}