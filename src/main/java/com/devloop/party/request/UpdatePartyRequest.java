package com.devloop.party.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePartyRequest {
    private String title;
    private String contents;
    private String status;
}
