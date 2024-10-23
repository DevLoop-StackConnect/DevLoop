package com.devloop.attachment.entity;

import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.enums.FileFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String imageURL;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FileFormat fileFormat;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Domain domain;

    public Attachment(String imageURL, FileFormat fileFormat, Domain domain) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.domain = domain;
    }
}

