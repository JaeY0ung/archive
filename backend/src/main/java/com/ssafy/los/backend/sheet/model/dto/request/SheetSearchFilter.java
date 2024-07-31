package com.ssafy.los.backend.sheet.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class SheetSearchFilter {

    private String keyword;

    private String sort;

    private Integer level;

}
