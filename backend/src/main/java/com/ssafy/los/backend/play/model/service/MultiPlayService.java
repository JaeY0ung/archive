package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.dto.request.MultiResultAfterDto;
import com.ssafy.los.backend.play.model.dto.request.MultiResultBeforeDto;
import com.ssafy.los.backend.play.model.dto.response.MultiResultListDto;
import com.ssafy.los.backend.user.model.entity.User;

import java.util.List;

public interface MultiPlayService {

    Long saveMultiPlayResult(MultiResultBeforeDto multiResultBeforeDto);

    Long completeMultiPlayResult(Long multiResultId, MultiResultAfterDto multiResultAfterDto);

    List<MultiResultListDto> getMultiPlayResultList(User user);

    Long removeMultiPlayResult(Long MultiPlayResultId);

}
