package com.devloop.attachment.service;

import com.devloop.attachment.entity.CommunityAttachment;
import com.devloop.attachment.repository.CommunityATMRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityAttachmentService {

    private final CommunityATMRepository communityATMRepository;

    //CommunityId로 첨부파일 조회
    public CommunityAttachment findCommunityAttachmentByCommunityId(Long communityId){
        return communityATMRepository.findByCommunityId(communityId)
                .orElse(null);
    }
    public void deleteCommunityAttachment(CommunityAttachment communityAttachment){
        communityATMRepository.delete(communityAttachment);
    }
}
