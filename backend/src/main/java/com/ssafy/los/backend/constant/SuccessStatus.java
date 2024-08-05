package com.ssafy.los.backend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    SUCCESS("SUCCESS"),
    FAIL("FAIL");

    private final String value;
}
