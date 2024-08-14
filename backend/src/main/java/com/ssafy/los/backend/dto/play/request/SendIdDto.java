package com.ssafy.los.backend.dto.play.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendIdDto {

    private String sender;
    private Long resultId;

}
