package com.ssafy.los.backend.dto.sheet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SheetUpdateFormDto {

    private String title;
    private Integer level;

}
