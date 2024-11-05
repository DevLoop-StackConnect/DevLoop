package com.devloop.attachment.entity;

import com.devloop.attachment.enums.FileFormat;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@Entity
@DiscriminatorValue("COMMUNITY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityAttachment extends Attachment {

    @Column(nullable = false)
    private Long communityId;

    private CommunityAttachment(Long communityId, URL imageURL, FileFormat fileFormat, String fileName){
        super(imageURL, fileFormat,  fileName);
        this.communityId = communityId;
    }
    public static CommunityAttachment of(Long communityId, URL imageURL, FileFormat fileFormat,String fileName){
        return new CommunityAttachment(communityId, imageURL, fileFormat,fileName);
    }
}
