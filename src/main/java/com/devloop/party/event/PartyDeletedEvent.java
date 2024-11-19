package com.devloop.party.event;

import com.devloop.party.entity.Party;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PartyDeletedEvent extends ApplicationEvent {
    private final Party party;

    public PartyDeletedEvent(Party party){
        super(party);
        this.party = party;
    }
}
