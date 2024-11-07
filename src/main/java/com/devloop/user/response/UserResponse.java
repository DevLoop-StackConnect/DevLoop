package com.devloop.user.response;

import com.devloop.community.response.CommunitySimpleResponse;
import com.devloop.party.response.GetPartyListResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.net.URL;
import java.util.List;

@Getter
public class UserResponse {

    @NotBlank
    private final String userName;

    @NotBlank
    private final String userEmail;

    @NotBlank
    private final String userRole;

    @NotNull
    private final URL url;

    private final List<GetPartyListResponse> partyList;
    private final List<CommunitySimpleResponse> communityList;
    private final String tutorRequestSubUrl;

    private UserResponse(String userName,
                         String userEmail,
                         String userRole,
                         URL url,
                         List<GetPartyListResponse> partyList,
                         List<CommunitySimpleResponse> communityList,
                         String tutorRequestSubUrl
    ) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.url = url;
        this.partyList = partyList;
        this.communityList = communityList;
        this.tutorRequestSubUrl = tutorRequestSubUrl;
    }

    public static UserResponse of(String userName,
                                  String userEmail,
                                  String userRole,
                                  URL url,
                                  List<GetPartyListResponse> partyList,
                                  List<CommunitySimpleResponse> communityList,
                                  String tutorRequestSubUrl
    ) {
        return new UserResponse(userName, userEmail, userRole, url, partyList, communityList, tutorRequestSubUrl);
    }
}
