package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultProfileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MultiPlayService {


    Long saveMultiPlayResult(MultiPlayResultBeforeDto multiResultBeforeDto);

    Long completeMultiPlayResult(Long multiResultId, MultiPlayResultAfterDto multiResultAfterDto);

    List<MultiPlayResultProfileDto> getMultiPlayResultList(Long userId);

    Long removeMultiPlayResult(Long MultiPlayResultId);

    String getLiveScore(MultipartFile file, Long sheetId, Long multiResultId) throws IllegalArgumentException;
}
