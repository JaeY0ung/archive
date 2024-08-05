package com.ssafy.los.backend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sort {
    POPULAR("POPULAR"),
    LATEST("LATEST"),
    HIGHEST_PLAY("HIGHEST_PLAY"),
    //    OLDEST("OLDEST"),
//    CHEAPEST("CHEAPEST"),
    HIGHEST_STAR_RATE("HIGHEST_STAR_RATE"),
    HIGHEST_VIEW("HIGHEST_VIEW"),
    HIGHEST_LEVEL("HIGHEST_LEVEL"),
    RANDOM("RANDOM");

    private final String value;
}
