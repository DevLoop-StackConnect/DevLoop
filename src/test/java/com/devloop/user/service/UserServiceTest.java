package com.devloop.user.service;

import com.devloop.attachment.repository.ProfileATMRepository;
import com.devloop.attachment.s3.S3Service;
import com.devloop.common.AuthUser;
import com.devloop.common.exception.ApiException;
import com.devloop.community.repository.CommunityRepository;
import com.devloop.party.repository.PartyRepository;
import com.devloop.tutor.entity.TutorRequest;
import com.devloop.tutor.repository.TutorRequestRepository;
import com.devloop.user.entity.User;
import com.devloop.user.enums.UserRole;
import com.devloop.user.repository.UserRepository;
import com.devloop.user.response.UserResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private ProfileATMRepository profileATMRepository;

    @Mock
    private TutorRequestRepository tutorRequestRepository;

    @Nested
    class getUserTest {
        @Test
        void ROLE_USER_유저_반환_테스트() {
            // Given
            AuthUser authUser = new AuthUser(1L, "skawlsgus2@naver.com", UserRole.ROLE_USER);
            User user = User.of("남진현", "skawlsgus2@naver.com", "123!!", UserRole.ROLE_USER);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // When
            UserResponse userResponse = userService.getUser(authUser);

            // Then
            assertNotNull(userResponse);
            assertEquals(userResponse.getUserEmail(), "skawlsgus2@naver.com");
            assertEquals(userResponse.getUserName(), "남진현");
            assertEquals("ROLE_USER", userResponse.getUserRole().toString());
            assertEquals("https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png", userResponse.getUrl().toString());
            assertEquals(userResponse.getPartyList().size(), 0);
            assertEquals(userResponse.getCommunityList().size(), 0);
            assertNull(userResponse.getTutorRequestSubUrl());
        }
        @Test
        void ROLE_TUTOR_유저_반환_테스트() {
            // Given
            AuthUser authUser = new AuthUser(1L, "skawlsgus2@naver.com", UserRole.ROLE_TUTOR);
            User user = User.of("남진현", "skawlsgus2@naver.com", "123!!", UserRole.ROLE_TUTOR);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            TutorRequest tutorRequest = TutorRequest.of("남진현", "https://exampleurl.net", "1", user);
            given(tutorRequestRepository.findByUserId(user.getId())).willReturn(Optional.of(tutorRequest));

            // When
            UserResponse userResponse = userService.getUser(authUser);

            // Then
            assertNotNull(userResponse);
            assertEquals(userResponse.getUserEmail(), "skawlsgus2@naver.com");
            assertEquals(userResponse.getUserName(), "남진현");
            assertEquals("ROLE_TUTOR", userResponse.getUserRole().toString());
            assertEquals("https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png", userResponse.getUrl().toString());
            assertEquals(userResponse.getPartyList().size(), 0);
            assertEquals(userResponse.getCommunityList().size(), 0);
            assertEquals(userResponse.getTutorRequestSubUrl(), "https://exampleurl.net");
        }
        @Test
        void ROLE_ADMIN_유저_반환_테스트() {
            // Given
            AuthUser authUser = new AuthUser(1L, "skawlsgus2@naver.com", UserRole.ROLE_ADMIN);
            User user = User.of("남진현", "skawlsgus2@naver.com", "123!!", UserRole.ROLE_ADMIN);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // When
            UserResponse userResponse = userService.getUser(authUser);

            // Then
            assertNotNull(userResponse);
            assertEquals(userResponse.getUserEmail(), "skawlsgus2@naver.com");
            assertEquals(userResponse.getUserName(), "남진현");
            assertEquals("ROLE_ADMIN", userResponse.getUserRole().toString());
            assertEquals("https://devloop-stackconnect1.s3.ap-northeast-2.amazonaws.com/defaultImg.png", userResponse.getUrl().toString());
            assertEquals(userResponse.getPartyList().size(), 0);
            assertEquals(userResponse.getCommunityList().size(), 0);
            assertNull(userResponse.getTutorRequestSubUrl());
        }
    }
    @Nested
    class updateProfileImgTest{
        @Test
        void 다수_파일_보낼때_에러테스트(){
            //Given
            AuthUser authUser = new AuthUser(1L, "skawlsgus2@naver.com", UserRole.ROLE_TUTOR);
            MockMultipartFile mockFile1 = new MockMultipartFile(
                    "file1",
                    "testfile1.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()
            );
            MockMultipartFile mockFile2 = new MockMultipartFile(
                    "file2",
                    "testfile2.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()
            );
            MultipartFile[] multipartFiles = new MockMultipartFile[]{mockFile1, mockFile2};

            // When and Then
            assertThrows(ApiException.class, () -> userService.updateProfileImg(multipartFiles, authUser));
        }
        @Test
        void 디퐆트이미지_업데이트_메서드호출_테스트(){
            //Given
            AuthUser authUser = new AuthUser(1L, "skawlsgus2@naver.com", UserRole.ROLE_TUTOR);
            MockMultipartFile mockFile1 = new MockMultipartFile(
                    "file1",
                    "testfile1.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()
            );
            MultipartFile[] multipartFiles = new MockMultipartFile[]{mockFile1};
            User user = User.of("남진현", "skawlsgus2@naver.com", "123!!", UserRole.ROLE_TUTOR);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            //When
            userService.updateProfileImg(multipartFiles, authUser);

            //then
            verify(profileATMRepository, times(0)).findById(any());
        }
    }
}


