package com.devloop.attachment.service;

import com.devloop.attachment.entity.PWTAttachment;
import com.devloop.attachment.repository.PWTATMRepository;
import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PWTAttachmentService {

    private final PWTATMRepository PWTATMRepository;

    // PWTId 로 PWT 첨부파일 조회
    public PWTAttachment findPwtAttachmentByPwtId(Long id) {
        return PWTATMRepository.findByPWTId(id)
                .orElse(null);
    }

    public void deletePwtAttachment(PWTAttachment pwtAttachment) {
        PWTATMRepository.delete(pwtAttachment);
    }
}
