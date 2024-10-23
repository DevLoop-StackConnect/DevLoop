package com.devloop.attachment.entity;

import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.enums.FileFormat;
import com.devloop.common.Timestamped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
@Table
public class Attachment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String imageURL;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FileFormat fileFormat;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Domain domain;

    private Attachment(String imageURL, FileFormat fileFormat, Domain domain) {
        this.imageURL = imageURL;
        this.fileFormat = fileFormat;
        this.domain = domain;
    }
    public static Attachment from(String imageURL, FileFormat fileFormat, Domain domain){
        return new Attachment(imageURL,fileFormat,domain);
    }

}
