package com.devloop.attachment.service;

import com.devloop.attachment.entity.CommunityAttachment;
import com.devloop.attachment.repository.CommunityATMRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityAttachmentService {

    private final CommunityATMRepository communityATMRepository;

    //CommunityId로 첨부파일 조회
    public CommunityAttachment findCommunityAttachmentByCommunityId(Long communityId){
        return communityATMRepository.findByCommunityId(communityId)
                .orElse(null);
    }

    @Transactional
    public void deleteCommunityAttachment(CommunityAttachment communityAttachment){
        communityATMRepository.delete(communityAttachment);
    }

    public CommunityATMRepository getCommunityATMRepository() {
        return communityATMRepository;
    }
}
