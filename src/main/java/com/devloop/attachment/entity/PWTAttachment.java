package com.devloop.attachment.entity;

import java.net.URL;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import com.devloop.attachment.enums.FileFormat;

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
