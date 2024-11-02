package com.devloop.attachment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileFormat {
    //영상
    MP4("mp4"),
    MV4("mv4"),
    MOV("mov"),
    //이미지
    JPG("jpg"),
    PNG("png"),
    JPEG("jpeg"),
    TXT("txt"),
    PDF("pdf"),
    GIF("gif");
    private final String format;
}
