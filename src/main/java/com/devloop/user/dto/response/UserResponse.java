package com.devloop.user.dto.response;

import com.devloop.party.response.GetPartyListResponse;
import com.devloop.user.enums.UserRole;
import lombok.Getter;
import java.net.URL;

@Getter
public class UserResponse {
    private final String userName;
    private final String userEmail;
    private final UserRole userRole;
    private final URL url;
    private final GetPartyListResponse partyList;



    private UserResponse( String userName,
                          String userEmail,
                          UserRole userRole,
                          URL url,
                          GetPartyListResponse partyList
                          ) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.url = url;
        this.partyList = partyList;

    }

    public static UserResponse from( String userName,
                                     String userEmail,
                                     UserRole userRole,
                                     URL url,
                                     GetPartyListResponse partyList
                                     ) {
        return new UserResponse(userName, userEmail, userRole, url, partyList);
    }
}
