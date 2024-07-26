package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.dto.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.play.model.dto.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.play.model.dto.response.MultiPlayResultListDto;
import com.ssafy.los.backend.user.model.entity.User;

import java.util.List;

public interface MultiPlayService {

    Long saveMultiPlayResult(MultiPlayResultBeforeDto multiResultBeforeDto);

    Long completeMultiPlayResult(Long multiResultId, MultiPlayResultAfterDto multiResultAfterDto);

    List<MultiPlayResultListDto> getMultiPlayResultList(User user);

    Long removeMultiPlayResult(Long MultiPlayResultId);

}
