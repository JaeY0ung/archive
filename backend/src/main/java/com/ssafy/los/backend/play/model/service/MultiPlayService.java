package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.dto.request.MultiResultAfterDto;
import com.ssafy.los.backend.play.model.dto.request.MultiResultBeforeDto;
import com.ssafy.los.backend.play.model.dto.request.SinglePlayRequestDto;
import com.ssafy.los.backend.play.model.dto.response.MultiPlayResultListDto;
import com.ssafy.los.backend.play.model.entity.MultiPlayResult;
import com.ssafy.los.backend.play.model.entity.SinglePlayResult;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MultiPlayService {

    Long saveMultiPlayResult(MultiResultBeforeDto multiResultBeforeDto);

    Long completeMultiPlayResult(Long multiResultId, MultiResultAfterDto multiResultAfterDto);

    List<MultiPlayResultListDto> getMultiPlayResultList(Long multiResultId);

    Long removeMultiPlayResult(Long MultiPlayResultId);

}
