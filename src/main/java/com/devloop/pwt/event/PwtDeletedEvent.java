package com.devloop.pwt.event;

import com.devloop.pwt.entity.ProjectWithTutor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PwtDeletedEvent extends ApplicationEvent {
    private final ProjectWithTutor projectWithTutor;

    public PwtDeletedEvent(ProjectWithTutor projectWithTutor){
        super(projectWithTutor);
        this.projectWithTutor = projectWithTutor;
    }
}
