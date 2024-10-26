package com.devloop.user.dto.response;

import com.devloop.party.response.GetPartyListResponse;
import lombok.Getter;
import java.net.URL;
import java.util.List;

@Getter
public class UserResponse {
    private final String userName;
    private final String userEmail;
    private final String userRole;
    private final URL url;
    private final List<GetPartyListResponse> partyList;/*
    private final CommunitySimpleResponse community;
    private final String tutorRequestSubUrl;*/



    private UserResponse( String userName,
                          String userEmail,
                          String userRole,
                          URL url,
                          List<GetPartyListResponse> partyList/*,
                          CommunitySimpleResponse community,
                          String tutorRequestSubUrl*/
                          ) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.url = url;
        this.partyList = partyList;
        /*this.community = community;
        this.tutorRequestSubUrl = tutorRequestSubUrl;*/
    }

    public static UserResponse of( String userName,
                                     String userEmail,
                                     String userRole,
                                     URL url,
                                     List<GetPartyListResponse> partyList/*,
                                     CommunitySimpleResponse community,
                                     String tutorRequestSubUrl*/
                                     ) {
        return new UserResponse(userName, userEmail, userRole, url, partyList/*, community, tutorRequestSubUrl*/);
    }
}
