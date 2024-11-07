package com.devloop.party.service;

import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.enums.Category;
import com.devloop.party.entity.Party;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.repository.PartyRepository;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.response.SavePartyResponse;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.*;
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

    @Test
    public void 파티_생성(){
        //given
        AuthUser authUser=new AuthUser(1L,"test@email.com", UserRole.ROLE_USER);
        SavePartyRequest savePartyRequest=SavePartyRequest.of(
                "제목",
                "내용",
                PartyStatus.COMPLETED,
                Category.WEB_DEV
        );
        User user= User.of("홍길동","Abc123!","test@email.com",UserRole.ROLE_USER);
        Party newParty= Party.from(savePartyRequest,user);

        //mocking
        given(userService.findByUserId(any())).willReturn(user);
        given(partyRepository.save(any())).willReturn(newParty);
        given(file.isEmpty()).willReturn(false);
        doNothing().when(s3Service).uploadFile(eq(file), eq(user), any(Party.class));

        //when
        SavePartyResponse savePartyResponse=partyService.saveParty(authUser,file,savePartyRequest);

        //then
        Assertions.assertNotNull(savePartyResponse);
        Assertions.assertEquals(newParty.getId(),savePartyResponse.getPartyId());
        verify(s3Service,times(1)).uploadFile(eq(file), eq(user), any(Party.class));
    }

}