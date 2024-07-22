package com.ssafy.los.backend.sheetOrder.model.dto;

import com.ssafy.los.backend.payment.model.entity.Payment;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
import java.sql.Timestamp;

public interface SheetOrderDto {

    Long getId();


    User getUser();


    Sheet getSheet();


    Payment getPayment();


    Timestamp getCreatedAt();
}
