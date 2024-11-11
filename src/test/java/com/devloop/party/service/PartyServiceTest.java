package com.devloop.party.service;

import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.s3.S3Service;
import com.devloop.attachment.service.PartyAttachmentService;
import com.devloop.common.AuthUser;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.enums.Category;
import com.devloop.common.exception.ApiException;
import com.devloop.party.entity.Party;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.request.UpdatePartyRequest;
import com.devloop.party.response.GetPartyDetailResponse;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.party.response.SavePartyResponse;
import com.devloop.party.response.UpdatePartyResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PartyServiceTest {
    @InjectMocks
    private PartyService partyService;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private UserService userService;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile file;

    @Mock
    private PartyAttachmentService partyAttachmentService;

    private AuthUser authUser;
    private User user;
    private Party party;
    private SavePartyRequest savePartyRequest;
    private PartyAttachment partyAttachment;

    @BeforeEach
    void setUp() throws Exception {
        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        user = User.of("홍길동", "Abc123!", "test@email.com", UserRole.ROLE_USER);

        Constructor<SavePartyRequest> constructor = SavePartyRequest.class.getDeclaredConstructor(
                String.class, String.class, PartyStatus.class, Category.class);
        constructor.setAccessible(true);

        savePartyRequest = constructor.newInstance(
                "제목",
                "내용",
                PartyStatus.COMPLETED,
                Category.WEB_DEV
        );
        party = Party.from(savePartyRequest, user);

        partyAttachment = PartyAttachment.of(
                1L,
                new URL("http://example.com/image.png"),
                FileFormat.PNG,
                "image.png"
        );
    }

    @Test
    void 스터디파티_등록_성공(){
        //given

        //mocking
        given(userService.findByUserId(anyLong())).willReturn(user);
        given(partyRepository.save(any())).willReturn(party);
        given(file.isEmpty()).willReturn(false);
        doNothing().when(s3Service).uploadFile(eq(file), eq(user), any(Party.class));

        //when
        SavePartyResponse savePartyResponse = partyService.saveParty(authUser, file, savePartyRequest);

        //then
        Assertions.assertNotNull(savePartyResponse);
        Assertions.assertEquals(party.getId(), savePartyResponse.getPartyId());
        Assertions.assertEquals(party.getTitle(),savePartyResponse.getTitle());
        Assertions.assertEquals(party.getContents(),savePartyResponse.getContents());
        verify(s3Service, times(1)).uploadFile(eq(file), eq(user), any(Party.class));
    }

    @Test
    void 파일_없을_때_스터디파티_수정_성공() throws Exception {
        //given
        Long partyId = 1L;
        user.setId(1L);
        Constructor<UpdatePartyRequest> constructor = UpdatePartyRequest.class.getDeclaredConstructor(
                String.class, String.class, PartyStatus.class, Category.class);
        constructor.setAccessible(true);

        UpdatePartyRequest updatePartyRequest = constructor.newInstance(
                "수정된 제목",
                "수정된 내용",
                PartyStatus.COMPLETED,
                Category.WEB_DEV
        );

        given(userService.findByUserId(anyLong())).willReturn(user);
        given(partyRepository.findById(anyLong())).willReturn(Optional.of(party));
        given(file.isEmpty()).willReturn(true);

        //when
        UpdatePartyResponse updatePartyResponse = partyService.updateParty(authUser, partyId, file, updatePartyRequest);

        //then
        Assertions.assertNotNull(updatePartyResponse);
        Assertions.assertEquals("수정된 제목", updatePartyResponse.getTitle());
        Assertions.assertEquals("수정된 내용",updatePartyResponse.getContents());
    }

    @Test
    void 파일_존재_단건_조회_성공(){
        //given
        Long partyId = 1L;
        given(partyRepository.findById(anyLong())).willReturn(Optional.of(party));
        given(partyAttachmentService.findPartyAttachmentByPartyId(anyLong())).willReturn(Optional.of(partyAttachment));
        //when
        GetPartyDetailResponse getPartyDetailResponse = partyService.getParty(partyId);

        //then
        Assertions.assertNotNull(getPartyDetailResponse);
        Assertions.assertEquals(party.getId(), getPartyDetailResponse.getPartyId());
        Assertions.assertEquals(partyAttachment.getImageURL().toString(), getPartyDetailResponse.getImageUrl().toString());
    }

    @Test
    void 제목포함_다건_조회_성공() {
        //given
        String title = "테스트";
        int page = 1;
        int size = 10;
        PageRequest pageable = PageRequest.of(page - 1, size);

        List<Party> partyList = List.of(party);
        Page<Party> parties = new PageImpl<>(List.of(party), pageable, partyList.size());
        given(partyRepository.findByTitleContaining(title, pageable)).willReturn(parties);

        //when
        Page<GetPartyListResponse> partyListResponsePage = partyService.getPartyList(title, page, size);

        //then
        Assertions.assertNotNull(partyListResponsePage);
        Assertions.assertEquals(1, partyListResponsePage.getTotalElements());
        Assertions.assertEquals(party.getTitle(), partyListResponsePage.getContent().get(0).getTitle());
    }

    @Test
    void 파일_존재_삭제_성공() {
        //given
        Long partyId = 1L;
        user.setId(1L);
        given(partyRepository.findById(anyLong())).willReturn(Optional.of(party));
        given(partyAttachmentService.findPartyAttachmentByPartyId(anyLong())).willReturn(Optional.of(partyAttachment));

        //when
        partyService.deleteParty(authUser, partyId);

        //then
        verify(s3Service, times(1)).delete(partyAttachment.getFileName());
        verify(partyAttachmentService, times(1)).deletePartyAttachment(partyAttachment);
        verify(partyRepository, times(1)).delete(party);
    }

    @Test
    void 권한_없는_사용자_삭제_예외() {
        //given
        Long partyId = 1L;
        user.setId(2L); //다른 사용자
        given(partyRepository.findById(anyLong())).willReturn(Optional.of(party));

        //when & then
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> partyService.deleteParty(authUser, partyId));
        Assertions.assertEquals(ErrorStatus._PERMISSION_DENIED, apiException.getErrorCode());
    }

}

