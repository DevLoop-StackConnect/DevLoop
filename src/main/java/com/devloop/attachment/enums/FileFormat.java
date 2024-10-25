package com.devloop.attachment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileFormat {
    JPG("jpg"),
    PNG("png"),
    JPEG("jpeg"),
    TXT("txt"),
    PDF("pdf");

    private final String format;
}
