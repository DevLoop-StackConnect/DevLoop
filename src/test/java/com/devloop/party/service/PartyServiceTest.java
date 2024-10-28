package com.devloop.party.service;

import com.devloop.attachment.entity.PartyAttachment;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.attachment.repository.PartyAMTRepository;
import com.devloop.attachment.s3.S3Service;
import com.devloop.attachment.service.PartyAttachmentService;
import com.devloop.common.AuthUser;
import com.devloop.party.entity.Party;
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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.awaitility.Awaitility.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PartyServiceTest {
    @Mock
    private PartyRepository partyRepository;
    @Mock
    private UserService userService;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private PartyAttachmentService partyAttachmentService;
    @InjectMocks
    public PartyService partyService;

    private AuthUser authUser;
    private User user;
    private Party party;
    private SavePartyRequest savePartyRequest;
    private UpdatePartyRequest updatePartyRequest;
    private PartyAttachment partyAttachment;
    private MultipartFile file;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        authUser=new AuthUser(1L,"abc@email.com",UserRole.ROLE_USER);
        savePartyRequest=new SavePartyRequest("파티 제목","파티 내용","IN_PROGRESS","WEB_DEV");
        updatePartyRequest=new UpdatePartyRequest("수정된 파티 제목","수정된 파티 내용","IN_PROGRESS","WEB_DEV");
        user=User.of("홍길동","abc@eamil.com",passwordEncoder.encode("Abc1234!"),UserRole.ROLE_USER);
        party=Party.from(savePartyRequest,user);
        String imageURL="https://example.com/image.PNG";
        file=mock(MultipartFile.class);
        partyAttachment=PartyAttachment.of(1L, new URL(imageURL), FileFormat.PNG,"image.PNG");
    }
    @Test
    public void 파티_저장_정상동작(){
        //given
        when(userService.findByUserId(anyLong())).thenReturn(user);
        when(partyRepository.save(any(Party.class))).thenReturn(party);
        when(file.isEmpty()).thenReturn(false);

        //when
        SavePartyResponse response=partyService.saveParty(authUser,file,savePartyRequest);

        //then
        Assertions.assertEquals(party.getId(),response.getPartyId());
        Assertions.assertEquals(party.getTitle(),response.getTitle());
        verify(s3Service,times(1)).uploadFile(eq(file),eq(user),any(Party.class));
    }

    @Test
    public void 파티_수정_정상동작() {

    }

    @Test
    public void 파티_다건조회_정상동작(){

    }

    @Test
    public void 파티_단건조회_정상동작() throws MalformedURLException {
        //given
        Long partyId=1L;
        when(partyRepository.findById(anyLong())).thenReturn(Optional.of(party));
        when(partyAttachmentService.findPartyAttachmentByPartyId(anyLong())).thenReturn(partyAttachment);

        //when
        GetPartyDetailResponse response=partyService.getParty(partyId);
        
        //then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(party.getId(),response.getPartyId());
        Assertions.assertEquals(partyAttachment.getImageURL(),new URL(response.getImageUrl()));
    }

    @Test
    public void 파티_삭제_정상동작(){

    }
}