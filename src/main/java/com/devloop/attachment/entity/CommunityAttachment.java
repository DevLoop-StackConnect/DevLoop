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
@DiscriminatorValue("C")
public class CommunityAttachment extends Attachment {

    @NotNull
    private Long communityId;

    private CommunityAttachment(Long communityId, URL imageURL, FileFormat fileFormat, Domain domain,String fileName){
        super(imageURL, fileFormat, domain, fileName);
        this.communityId = communityId;
    }
    public static CommunityAttachment from(Long communityId, URL imageURL, FileFormat fileFormat, Domain domain,String fileName){
        return new CommunityAttachment(communityId, imageURL, fileFormat, domain,fileName);
    }
}
