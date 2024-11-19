package com.devloop.party.event;

import com.devloop.party.entity.Party;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PartyCreatedEvent extends ApplicationEvent {
    private final Party party;

    public PartyCreatedEvent(Party party){
        super(party);
        this.party = party;
    }
}
