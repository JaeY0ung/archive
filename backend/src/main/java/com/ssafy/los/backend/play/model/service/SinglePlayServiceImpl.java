package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.repository.SinglePlayResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SinglePlayServiceImpl implements SinglePlayService {

    private final SinglePlayResultRepository singlePlayResultRepository;
}
