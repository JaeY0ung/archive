package com.ssafy.los.backend.dto.sheet.request;

import com.ssafy.los.backend.constant.Sort;
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

    private Sort sort;

    private Integer[] level;

}
