package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.repository.MultiPlayResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultiPlayServiceImpl implements MultiPlayService {

    private final MultiPlayResultRepository multiPlayResultRepository;
}
