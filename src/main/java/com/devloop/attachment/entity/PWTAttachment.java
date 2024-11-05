package com.devloop.attachment.entity;

import com.devloop.attachment.enums.FileFormat;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

@Getter
@Entity
@DiscriminatorValue("PWT")
@Table(name = "pwt_attachment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PWTAttachment extends Attachment {

    @Column(nullable = false)
    private Long PWTId;

    private PWTAttachment(Long PWTId, URL imageURL, FileFormat fileFormat, String fileName) {
        super(imageURL, fileFormat, fileName);
        this.PWTId = PWTId;
    }

    public static PWTAttachment of(Long PWTId, URL imageURL, FileFormat fileFormat, String fileName) {
        return new PWTAttachment(PWTId, imageURL, fileFormat, fileName);
    }


}
