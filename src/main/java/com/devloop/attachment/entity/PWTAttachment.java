package com.devloop.attachment.entity;

import com.devloop.attachment.enums.Domain;
import com.devloop.attachment.enums.FileFormat;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("S")
public class PWTAttachment extends Attachment {

    @NotNull
    private Long PWTId;

    private PWTAttachment(Long PWTId, URL imageURL, FileFormat fileFormat, Domain domain, String fileName){
        super(imageURL, fileFormat, domain,fileName);
        this.PWTId = PWTId;
    }

    public static PWTAttachment from(Long PWTId, URL imageURL, FileFormat fileFormat, Domain domain,String fileName){
        return new PWTAttachment(PWTId, imageURL, fileFormat, domain,fileName);
    }
}
