package com.devloop.community.event;

import com.devloop.community.entity.Community;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommunityCreatedEvent extends ApplicationEvent{
    private final Community community;

    public CommunityCreatedEvent(Community community){
        super(community);
        this.community = community;
    }
}
