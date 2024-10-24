package com.devloop.attachment.entity;

import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.enums.FileFormat;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("S")
public class StudyAttachment extends Attachment {
    private StudyAttachment(URL imageURL, FileFormat fileFormat, Domain domain, String fileName){
        super(imageURL, fileFormat, domain,fileName);
    }

    public static StudyAttachment from(URL imageURL, FileFormat fileFormat, Domain domain,String fileName){
        return new StudyAttachment(imageURL, fileFormat, domain,fileName);
    }
}
