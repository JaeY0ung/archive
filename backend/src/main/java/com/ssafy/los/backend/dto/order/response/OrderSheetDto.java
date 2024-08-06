package com.ssafy.los.backend.dto.order.response;

import com.ssafy.los.backend.constant.PayType;
import lombok.Data;

import java.util.List;

@Data
public class OrderSheetDto {

    private List<Long> sheetIds;
    private PayType payType;
    private Long totalPrice;
}
