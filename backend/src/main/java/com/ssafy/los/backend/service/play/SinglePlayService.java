package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.dto.play.request.SingleResultAfterDto;
import com.ssafy.los.backend.dto.play.request.SingleResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.SingePlayResultProfileDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface SinglePlayService {

    Long saveSinglePlayResult(SingleResultBeforeDto singleResultBeforeDto);

    Long completeSinglePlayResult(Long singleResultId, SingleResultAfterDto singleResultAfterDto);

    Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable);

    List<SingePlayResultProfileDto> getSinglePlayResultListByUser(Long userId);

    Long removeSinglePlayResult(Long singlePlayResultId);

    String getLiveScore(MultipartFile file, Long sheetId, Long singleResultId)
            throws IllegalArgumentException;
}
