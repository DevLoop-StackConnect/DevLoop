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
@DiscriminatorValue("C")
public class CommunityAttachment extends Attachment {
    private CommunityAttachment(URL imageURL, FileFormat fileFormat, Domain domain,String fileName){
        super(imageURL, fileFormat, domain, fileName);
    }
    public static CommunityAttachment from(URL imageURL, FileFormat fileFormat, Domain domain,String fileName){
        return new CommunityAttachment(imageURL, fileFormat, domain,fileName);
    }
}
