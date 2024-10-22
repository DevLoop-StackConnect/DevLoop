package com.devloop.party.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PartyStatus {
    IN_PROGRESS("모집중"),
    COMPLETED("모집완료");

    private final String partyStatus;
}
