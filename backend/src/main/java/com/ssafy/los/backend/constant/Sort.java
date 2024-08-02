package com.ssafy.los.backend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sort {
    POPULAR("POPULAR"),
    OLDEST("OLDEST"),
    CHEAPEST("CHEAPEST"),
    HIGHEST_VIEW("HIGHEST_VIEW"),
    LATEST("LATEST"),
    RANDOM("RANDOM"),
    STAR_RATE_HIGHEST("STAR_RATE_HIGHEST");

    private final String value;
}
