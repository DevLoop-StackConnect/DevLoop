package com.devloop.common.apipayload.status;

import com.devloop.common.apipayload.BaseCode;
import com.devloop.common.apipayload.dto.ReasonDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK, "200", "Ok");

    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String message;

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .statusCode(statusCode)
                .message(message)
                .httpStatus(httpStatus)
                .success(true)
                .build();
    }
}