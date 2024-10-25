package com.devloop.attachment.entity;

import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.enums.FileFormat;
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
public abstract class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private URL imageURL;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FileFormat fileFormat;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Domain domain;

    @NotNull
    private String fileName;

    public Attachment(URL imageURL, FileFormat fileFormat, Domain domain,String fileName) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.domain = domain;
        this.fileName = fileName;
    }
}

