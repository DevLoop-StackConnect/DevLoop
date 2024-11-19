package com.devloop.lecture.event;

import com.devloop.lecture.entity.Lecture;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LectureUpdatedEvent extends ApplicationEvent {
    private final Lecture lecture;

    public LectureUpdatedEvent(Lecture lecture){
        super(lecture);
        this.lecture = lecture;
    }

}
