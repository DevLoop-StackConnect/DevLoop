package com.devloop.attachment.entity;

import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.Timestamped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Attachment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private URL imageURL;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FileFormat fileFormat;

    @NotNull
    private String fileName;

    public Attachment(URL imageURL, FileFormat fileFormat,String fileName) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.fileName = fileName;
    }
}

