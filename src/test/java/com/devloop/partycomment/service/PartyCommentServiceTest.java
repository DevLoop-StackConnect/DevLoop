package com.devloop.partycomment.service;

import com.devloop.common.AuthUser;
import com.devloop.common.enums.Category;
import com.devloop.party.entity.Party;
import com.devloop.party.enums.PartyStatus;
import com.devloop.party.request.SavePartyRequest;
import com.devloop.party.service.PartyService;
import com.devloop.partycomment.entity.PartyComment;
import com.devloop.partycomment.repository.PartyCommentRepository;
import com.devloop.partycomment.request.SavePartyCommentRequest;
import com.devloop.partycomment.request.UpdatePartyCommentRequest;
import com.devloop.partycomment.response.GetPartyCommentListResponse;
import com.devloop.partycomment.response.SavePartyCommentResponse;
import com.devloop.partycomment.response.UpdatePartyCommentResponse;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartyCommentServiceTest {
    @InjectMocks
    private PartyCommentService partyCommentService;

    @Mock
    private UserService userService;

    @Mock
    private PartyService partyService;

    @Mock
    private PartyCommentRepository partyCommentRepository;

    private AuthUser authUser;
    private User user;
    private SavePartyRequest savePartyRequest;
    private SavePartyCommentRequest savePartyCommentRequest;
    private PartyComment partyComment;
    private Party party;

    @BeforeEach
    void setUp() throws Exception {
        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        user = User.of("홍길동", "Abc123!", "test@email.com", UserRole.ROLE_USER);

        Constructor<SavePartyRequest> partyConstructor = SavePartyRequest.class.getDeclaredConstructor(
                String.class, String.class, PartyStatus.class, Category.class);
        partyConstructor.setAccessible(true);
        savePartyRequest = partyConstructor.newInstance(
                "제목",
                "내용",
                PartyStatus.COMPLETED,
                Category.WEB_DEV
        );
        party = Party.from(savePartyRequest, user);

        Constructor<SavePartyCommentRequest> commentConstructor = SavePartyCommentRequest.class.getDeclaredConstructor(
                String.class);
        commentConstructor.setAccessible(true);
        savePartyCommentRequest = commentConstructor.newInstance(
                "댓글"
        );
        partyComment = PartyComment.from(savePartyCommentRequest, user, party);

    }

    @Test
    void 댓글_등록_성공() {
        //given
        Long partyId = 1L;
        given(userService.findByUserId(any())).willReturn(user);
        given(partyService.findById(any())).willReturn(party);
        given(partyCommentRepository.save(any())).willReturn(partyComment);

        //when
        SavePartyCommentResponse savePartyCommentResponse = partyCommentService.savePartyComment(authUser, partyId, savePartyCommentRequest);

        //then
        Assertions.assertNotNull(savePartyCommentResponse);
        Assertions.assertEquals(partyComment.getId(), savePartyCommentResponse.getCommentId());
        Assertions.assertEquals(partyComment.getComment(), savePartyCommentResponse.getComment());
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        //given
        Long partyId = 1L;
        Long commentId = 1L;
        user.setId(1L);

        Constructor<UpdatePartyCommentRequest> constructor = UpdatePartyCommentRequest.class.getDeclaredConstructor(
                String.class);
        constructor.setAccessible(true);
        UpdatePartyCommentRequest updatePartyCommentRequest = constructor.newInstance(
                "수정된 댓글"
        );

        given(partyService.findById(any())).willReturn(party);
        given(partyCommentRepository.findById(any())).willReturn(Optional.of(partyComment));

        //when
        UpdatePartyCommentResponse updatePartyCommentResponse = partyCommentService.updatePartyComment(authUser, partyId, commentId, updatePartyCommentRequest);

        //then
        Assertions.assertNotNull(updatePartyCommentResponse);
    }

    @Test
    void 댓글_다건_조회_성공() {
        //given
        Long partyId = 1L;
        int page = 1;
        int size = 10;
        PageRequest pageable = PageRequest.of(page - 1, size);

        ReflectionTestUtils.setField(party, "id", partyId);
        List<PartyComment> partyCommentList = List.of(partyComment);
        Page<PartyComment> partyComments = new PageImpl<>(List.of(partyComment), pageable, partyCommentList.size());
        given(partyService.findById(any())).willReturn(party);
        given(partyCommentRepository.findByPartyId(partyId, pageable)).willReturn(partyComments);

        //when
        Page<GetPartyCommentListResponse> partyCommentListResponsePage = partyCommentService.getPartyCommentList(partyId, page, size);

        //then
        Assertions.assertNotNull(partyCommentListResponsePage);
    }

    @Test
    void 댓글_삭제_성공() {
        //given
        Long partyId = 1L;
        Long commentId = 1L;
        user.setId(1L);

        given(partyService.findById(anyLong())).willReturn(party);
        given(partyCommentRepository.findById(anyLong())).willReturn(Optional.of(partyComment));

        //when
        partyCommentService.deletePartyComment(authUser, partyId, commentId);

        //then
        verify(partyCommentRepository, times(1)).delete(partyComment);
    }

}