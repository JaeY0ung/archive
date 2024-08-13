package com.ssafy.los.backend.domain.repository.play;

public interface CustomMultiPlayRepository {

    Long calculateCountOfWinMultiPlayResultByUserId(Long userId);

    Long calculateCountOfLostMultiPlayResultByUserId(Long userId);
}
