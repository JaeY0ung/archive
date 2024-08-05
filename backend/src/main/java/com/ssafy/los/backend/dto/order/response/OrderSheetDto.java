package com.ssafy.los.backend.dto.order.response;

import com.ssafy.los.backend.constant.PayType;
import java.util.List;
import lombok.Data;

@Data
public class OrderSheetDto {

    private List<Long> sheetIds;
    private PayType payType;
}
