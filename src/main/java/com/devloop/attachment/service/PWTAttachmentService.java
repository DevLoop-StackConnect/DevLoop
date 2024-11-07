package com.devloop.attachment.service;

import com.devloop.attachment.entity.PWTAttachment;
import com.devloop.attachment.repository.PWTATMRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PWTAttachmentService {

    private final PWTATMRepository PWTATMRepository;

    // PWTId 로 PWT 첨부파일 조회
    public PWTAttachment findPwtAttachmentByPwtId(Long id) {
        return PWTATMRepository.findByPWTId(id)
                .orElse(null);
    }

    @Transactional
    public void deletePwtAttachment(PWTAttachment pwtAttachment) {
        PWTATMRepository.delete(pwtAttachment);
    }

    @Transactional
    public void savePwtAttachment(PWTAttachment pwtAttachment) {
        PWTATMRepository.save(pwtAttachment);
    }
}
