package com.devloop.lecture.entity;

import com.devloop.common.Timestamped;
import com.devloop.lecture.enums.VideoStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.sql.Time;

@Entity
@NoArgsConstructor
@Getter
public class LectureVideo extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private URL videoURL;

    @NotNull
    private String fileName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VideoStatus status=VideoStatus.PENDING;

//    @NotNull
//    private Integer sequence;

    @ManyToOne
    @JoinColumn(name="lecture_id")
    private Lecture lecture;

    private LectureVideo(URL videoURL,String fileName,VideoStatus status, Lecture lecture){
        this.videoURL=videoURL;
        this.fileName=fileName;
        this.status=status;
        this.lecture=lecture;
    }
    public static LectureVideo of(URL videoURL,String fileName,VideoStatus status, Lecture lecture){
        return new LectureVideo(
                videoURL,
                fileName,
                status,
                lecture
        );
    }

}
