package com.ssafy.los.backend.config.scheduler;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.service.sheet.DifficultyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
//@Component
@RequiredArgsConstructor
public class DifficultyScheduler {

    private final DifficultyService difficultyService;
    private final SheetRepository sheetRepository;

//    @Scheduled(cron = "0 0 0 * * ?") // 자정마다 실행
//    @Scheduled(fixedRate =  30 * 1000)
    public void run() {
        log.info("difficultyService 스케줄러가 작동됩니다.");

        // id만 반환하는 서비스 만들기
        List<Sheet> sheetList = sheetRepository.findAll();

        for (Sheet sheet : sheetList) {
            Long sheetId = sheet.getId();
            try {
                difficultyService.calculateDifficulty(sheetId);
                log.info("Sheet ID {}의 difficulty 계산이 완료되었습니다.", sheetId);
            } catch (Exception e) {
                log.error("Sheet ID {}의 difficulty 계산 중 오류 발생: {}", sheetId, e.getMessage());
            }
        }

        log.info("difficultyService 스케줄러가 모든 sheet에 대해 완료되었습니다.");

    }
}
