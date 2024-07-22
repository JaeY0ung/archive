package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.service.SinglePlayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SinglePlayController {

    private final SinglePlayService singlePlayService;
}
