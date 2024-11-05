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
@DiscriminatorValue("PROFILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileAttachment extends Attachment {

    @Column(nullable = false)
    private Long userId;

    private ProfileAttachment(Long userId, URL imageURL, FileFormat fileFormat, String fileName) {
        super(imageURL, fileFormat, fileName);
        this.userId = userId;
    }

    public static ProfileAttachment of(Long userId, URL imageURL, FileFormat fileFormat, String fileName) {
        return new ProfileAttachment(userId, imageURL, fileFormat, fileName);
    }
}
