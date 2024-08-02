package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultListDto;
import java.util.List;

public interface MultiPlayService {

    Long saveMultiPlayResult(MultiPlayResultBeforeDto multiResultBeforeDto);

    Long completeMultiPlayResult(Long multiResultId, MultiPlayResultAfterDto multiResultAfterDto);

    List<MultiPlayResultListDto> getMultiPlayResultList(User user);

    Long removeMultiPlayResult(Long MultiPlayResultId);

}
