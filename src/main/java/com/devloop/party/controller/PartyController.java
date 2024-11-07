package com.devloop.party.controller;

import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.ApiResponse;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.party.response.GetPartyDetailResponse;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.party.response.SavePartyResponse;
import com.devloop.party.response.UpdatePartyResponse;
import com.devloop.party.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PartyController {
    private final PartyService partyService;

    //스터디 파티 모집 게시글 등록
    @PostMapping("/v1/parties")
    public ApiResponse<SavePartyResponse> saveParty(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @ModelAttribute SavePartyRequest savePartyRequest
    ){
        return ApiResponse.ok(partyService.saveParty(authUser,file,savePartyRequest));
    }

    //스터디 파티 게시글 수정
    @PatchMapping("/v1/parties/{partyId}")
    public ApiResponse<UpdatePartyResponse> updateParty(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long partyId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @ModelAttribute UpdatePartyRequest updatePartyRequest
    ){
        return ApiResponse.ok(partyService.updateParty(authUser,partyId,file,updatePartyRequest));
    }

    //스터디 파티 모집 다건 조회
    @GetMapping("/search/v1/parties")
    public ApiResponse<Page<GetPartyListResponse>> getPartyList(
        @RequestParam(required = false) String title,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        return ApiResponse.ok(partyService.getPartyList(title,page,size));
    }

    //스터디 파티 모집 게시글 단건 조회
    @GetMapping("/search/v1/parties/{partyId}")
    public ApiResponse<GetPartyDetailResponse> getParty(
            @PathVariable Long partyId
    ){
        return ApiResponse.ok(partyService.getParty(partyId));
    }

    //파티모집 게시글 삭제
    @DeleteMapping("/v1/parties/{partyId}")
    public ResponseEntity<Void> deleteParty(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long partyId) {
        partyService.deleteParty(authUser, partyId);
        return ResponseEntity.noContent().build();
    }
}
