package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.dto.request.SingleRequestDto;
import com.ssafy.los.backend.play.model.entity.SinglePlayResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SinglePlayService {

    Long saveSinglePlayResult(SingleRequestDto singlePlayRequestDto);

//    void getSinglePlayResult();

    Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable);

    Long removeSinglePlayResult(Long singlePlayResultId);

}
