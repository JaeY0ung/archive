package com.ssafy.los.backend.sheet.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SheetSearchFilter {

    private String keyword;

    private String sort; // "popular", "new"

    private Integer count;

    private Integer page;
}
