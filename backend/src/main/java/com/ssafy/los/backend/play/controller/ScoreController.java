package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.dto.MultiPlayScoreDto;
import com.ssafy.los.backend.play.model.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @GetMapping(path = "/battle/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MultiPlayScoreDto> aggregate() {
        return scoreService.stream();
    }
}
