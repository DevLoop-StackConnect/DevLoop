package com.devloop.pwt.event;

import com.devloop.pwt.entity.ProjectWithTutor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PwtCreatedEvent extends ApplicationEvent {
    private final ProjectWithTutor projectWithTutor;

    public PwtCreatedEvent(ProjectWithTutor projectWithTutor){
        super(projectWithTutor);
        this.projectWithTutor = projectWithTutor;
    }
}
