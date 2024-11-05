package com.devloop.attachment.entity;

import java.net.URL;
import lombok.Getter;
import lombok.AccessLevel;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.devloop.common.Timestamped;
import com.devloop.attachment.enums.FileFormat;

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

    public Attachment(URL imageURL, FileFormat fileFormat, String fileName) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.fileName = fileName;
    }

    public void updateAttachment(URL imageURL, FileFormat fileFormat, String fileName) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.fileName = fileName;
    }
}

