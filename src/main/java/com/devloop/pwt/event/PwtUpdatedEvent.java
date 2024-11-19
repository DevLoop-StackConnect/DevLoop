package com.devloop.pwt.event;

import com.devloop.pwt.entity.ProjectWithTutor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PwtUpdatedEvent extends ApplicationEvent {
    private final ProjectWithTutor projectWithTutor;

    public PwtUpdatedEvent(ProjectWithTutor projectWithTutor){
        super(projectWithTutor);
        this.projectWithTutor = projectWithTutor;
    }
}
