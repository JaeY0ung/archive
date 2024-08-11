package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.dto.play.request.SinglePlayRequestDto;
import com.ssafy.los.backend.dto.play.response.SingePlayResultProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SinglePlayService {

    Long saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto);

    Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable);

    List<SingePlayResultProfileDto> getSinglePlayResultListByUser(Long userId);

    Long removeSinglePlayResult(Long singlePlayResultId);

}
