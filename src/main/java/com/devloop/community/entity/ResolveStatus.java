package com.devloop.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResolveStatus {
    SOLVED("해결"),
    UNSOLVED("미해결");

    private final String description;

}


