package com.ssafy.los.backend.dto;

import com.ssafy.los.backend.domain.entity.Payment;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import java.sql.Timestamp;

public interface SheetOrderDto {

    Long getId();


    User getUser();


    Sheet getSheet();


    Payment getPayment();


    Timestamp getCreatedAt();
}
