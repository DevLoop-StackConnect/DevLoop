package com.devloop.attachment.service;

import com.devloop.attachment.repository.FARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FAService {
    private final FARepository faRepository;

}
