package com.ssafy.los.backend.like.controller;

import com.ssafy.los.backend.like.model.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

}
