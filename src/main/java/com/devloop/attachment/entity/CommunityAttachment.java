package com.devloop.attachment.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("C")
public class CommunityAttachment extends Attachment {
}
