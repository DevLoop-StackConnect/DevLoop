package com.devloop.party.request;


import com.devloop.party.enums.PartyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SavePartyRequest {
    private String title;
    private String contents;
    private String status;
}
