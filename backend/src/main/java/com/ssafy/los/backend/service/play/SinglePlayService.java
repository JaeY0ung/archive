package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.dto.play.request.SinglePlayRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SinglePlayService {

    Long saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto);

//    void getSinglePlayResult();

    Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable);

    Long removeSinglePlayResult(Long singlePlayResultId);

}
