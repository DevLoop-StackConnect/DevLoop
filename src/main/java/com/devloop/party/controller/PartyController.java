package com.devloop.party.controller;


import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.response.SavePartyResponse;
import com.devloop.party.service.PartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PartyController {
    private final PartyService partyService;


    @PostMapping("/v1/parties")
    public ResponseEntity<SavePartyResponse> saveParty(

            @ModelAttribute SavePartyRequest savePartyRequest
    ){
        return ResponseEntity.ok(partyService.saveParty(savePartyRequest));
    }




}
