package com.devloop.attachment.entity;

import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@Entity
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Attachment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private URL imageURL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileFormat fileFormat;

    @Column(nullable = false)
    private String fileName;

    public Attachment(URL imageURL, FileFormat fileFormat,String fileName) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.fileName = fileName;
    }

    public void updateAttachment(URL imageURL, FileFormat fileFormat,String fileName){
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.fileName = fileName;
    }
}

