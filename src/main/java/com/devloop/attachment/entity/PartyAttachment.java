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
@DiscriminatorValue("PARTY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyAttachment extends Attachment {

    @Column(nullable = false)
    private Long partyId;

    private PartyAttachment(Long partyId, URL imageURL, FileFormat fileFormat, String fileName) {
        super(imageURL, fileFormat, fileName);
        this.partyId = partyId;
    }

    public static PartyAttachment of(Long partyId, URL imageURL, FileFormat fileFormat, String fileName) {
        return new PartyAttachment(partyId, imageURL, fileFormat, fileName);
    }
}
