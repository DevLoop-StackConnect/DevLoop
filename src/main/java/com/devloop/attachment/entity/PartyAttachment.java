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
@DiscriminatorValue("PARTY")

public class PartyAttachment extends Attachment {

    @NotNull
    private Long partyId;

    private PartyAttachment(Long partyId, URL imageURL, FileFormat fileFormat , String fileName){
        super(imageURL, fileFormat, fileName);
        this.partyId = partyId;
    }
    public static PartyAttachment of(Long partyId, URL imageURL, FileFormat fileFormat ,String fileName){
        return new PartyAttachment(partyId, imageURL, fileFormat,fileName);
    }
}
