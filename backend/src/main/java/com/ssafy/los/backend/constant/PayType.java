package com.ssafy.los.backend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayType {
    KAKAO_PAY("KAKAO_PAY"),
    CASH("CASH");

    private final String value;
}
