package com.devloop.user.dto.response;

import com.devloop.community.dto.response.CommunitySimpleResponse;
import com.devloop.party.response.GetPartyListResponse;
import com.devloop.tutor.entity.TutorRequest;
import lombok.Getter;
import java.net.URL;

@Getter
public class UserResponse {
    private final String userName;
    private final String userEmail;
    private final String userRole;
    private final URL url;
    private final GetPartyListResponse partyList;
    private final CommunitySimpleResponse community;
    private final String tutorRequestSubUrl;



    private UserResponse( String userName,
                          String userEmail,
                          String userRole,
                          URL url,
                          GetPartyListResponse partyList,
                          CommunitySimpleResponse community,
                          String tutorRequestSubUrl
                          ) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.url = url;
        this.partyList = partyList;
        this.community = community;
        this.tutorRequestSubUrl = tutorRequestSubUrl;
    }

    public static UserResponse of( String userName,
                                     String userEmail,
                                     String userRole,
                                     URL url,
                                     GetPartyListResponse partyList,
                                     CommunitySimpleResponse community,
                                     String tutorRequestSubUrl
                                     ) {
        return new UserResponse(userName, userEmail, userRole, url, partyList, community, tutorRequestSubUrl);
    }
}
