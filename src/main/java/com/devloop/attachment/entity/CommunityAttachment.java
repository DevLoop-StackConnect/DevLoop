package com.devloop.attachment.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("COMMUNITY")
public class CommunityAttachment extends Attachment {
    @NotNull
    private Long communityId;

    private CommunityAttachment(Long communityId, URL imageURL, String fileFormat, String fileName){
        super(imageURL, fileFormat,  fileName);
        this.communityId = communityId;
    }
    public static CommunityAttachment of(Long communityId, URL imageURL, String fileFormat,String fileName){
        return new CommunityAttachment(communityId, imageURL, fileFormat,fileName);
    }
}
