package com.ssafy.los.backend.dto.sheet.request;

import com.ssafy.los.backend.constant.Sort;
import com.ssafy.los.backend.constant.SuccessStatus;
import java.util.HashSet;
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

    private Integer[] levels;

    private Integer[] genres;

    private Integer[] prices;

    private HashSet<SuccessStatus> successStatuses;

    private Sort sort;
}
