package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.dto.play.MultiPlayScoreDto;
import com.ssafy.los.backend.service.play.LiveScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ScoreController {

    private final LiveScoreService liveScoreService;

    @GetMapping(path = "/battle/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MultiPlayScoreDto> aggregate() {
        return liveScoreService.stream();
    }
}
