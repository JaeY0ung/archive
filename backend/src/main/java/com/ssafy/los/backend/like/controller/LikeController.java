package com.ssafy.los.backend.like.controller;

import com.ssafy.los.backend.like.model.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
}
