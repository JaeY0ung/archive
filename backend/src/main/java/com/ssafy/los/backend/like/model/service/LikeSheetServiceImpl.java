package com.ssafy.los.backend.like.model.service;

import com.ssafy.los.backend.like.model.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeSheetServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
}
