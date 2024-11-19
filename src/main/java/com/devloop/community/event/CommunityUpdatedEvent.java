package com.devloop.community.event;

import com.devloop.community.entity.Community;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommunityUpdatedEvent extends ApplicationEvent {
    private final Community community;

    public CommunityUpdatedEvent(Community community){
        super(community);
        this.community = community;
    }
}
