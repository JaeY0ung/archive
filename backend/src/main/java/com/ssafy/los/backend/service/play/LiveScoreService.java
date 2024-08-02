package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.dto.play.MultiPlayScoreDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LiveScoreService {

    private static final MultiPlayScoreDto multiPlayScoreDto = new MultiPlayScoreDto(-1);

    public Flux<MultiPlayScoreDto> stream() {

        return Flux.interval(Duration.ofMillis(100)) // 시간 간격 지정.
                .map(it -> {
                    if (multiPlayScoreDto.getScore() == -1) {
                        multiPlayScoreDto.setScore(0);
                    } else if (multiPlayScoreDto.getScore() > 100) {
                        multiPlayScoreDto.setScore(100);
                    } else if (multiPlayScoreDto.getScore() == 100) {
                        multiPlayScoreDto.setScore(10000);
                    } else {

                        int total = multiPlayScoreDto.getScore() + (int) Math.floor(
                                Math.random() * 10);

                        if (total <= 100) {
                            multiPlayScoreDto.setScore(total);
                        } else {
                            multiPlayScoreDto.setScore(100);
                        }
                    }
                    return multiPlayScoreDto;
                })
                .takeWhile(scoreDto -> scoreDto.getScore() != 10000);

    }
}
