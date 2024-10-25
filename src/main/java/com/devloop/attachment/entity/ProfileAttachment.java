package com.devloop.attachment.entity;

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
@DiscriminatorValue("PROFILE")

public class ProfileAttachment extends Attachment {

    @NotNull
    private Long userId;

    private ProfileAttachment(Long userId, URL imageURL, String fileFormat ,  String fileName){
        super(imageURL, fileFormat,fileName);
        this.userId = userId;
    }

    public static ProfileAttachment of(Long userId, URL imageURL, String fileFormat ,String fileName){
        return new ProfileAttachment(userId, imageURL, fileFormat,fileName);
    }
}
