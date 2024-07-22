package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.service.MultiPlayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MultiPlayController {

    private final MultiPlayService multiPlayService;
}
