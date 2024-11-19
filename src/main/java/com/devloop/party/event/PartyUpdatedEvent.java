package com.devloop.party.event;

import com.devloop.party.entity.Party;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PartyUpdatedEvent extends ApplicationEvent {
    private final Party party;

    public PartyUpdatedEvent(Party party){
        super(party);
        this.party = party;
    }
}
