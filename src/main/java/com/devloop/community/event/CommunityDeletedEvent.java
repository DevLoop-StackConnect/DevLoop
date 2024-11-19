package com.devloop.community.event;

import com.devloop.community.entity.Community;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommunityDeletedEvent extends ApplicationEvent {
    private final Community community;

    public CommunityDeletedEvent(Community community){
        super(community);
        this.community = community;
    }
}
