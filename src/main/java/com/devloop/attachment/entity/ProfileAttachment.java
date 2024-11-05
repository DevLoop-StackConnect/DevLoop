package com.devloop.attachment.entity;

import java.net.URL;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import com.devloop.attachment.enums.FileFormat;

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
