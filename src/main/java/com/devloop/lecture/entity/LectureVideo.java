package com.devloop.lecture.entity;

import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.Timestamped;
import com.devloop.lecture.enums.VideoStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureVideo extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String fileName;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VideoStatus status=VideoStatus.PENDING;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileFormat fileFormat;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lecture_id", nullable = false)
    private Lecture lecture;

    private LectureVideo(String fileName,String title,VideoStatus status, FileFormat fileFormat, Lecture lecture){
        this.fileName=fileName;
        this.title=title;
        this.status=status;
        this.fileFormat=fileFormat;
        this.lecture=lecture;
    }

    public static LectureVideo of(String fileName,String title,VideoStatus status,FileFormat fileFormat, Lecture lecture){
        return new LectureVideo(
                fileName,
                title,
                status,
                fileFormat,
                lecture
        );
    }

}
