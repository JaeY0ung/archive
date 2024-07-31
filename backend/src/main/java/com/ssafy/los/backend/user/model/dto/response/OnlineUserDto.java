package com.ssafy.los.backend.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OnlineUserDto {

    private Long id;
    private String userImg;
    private String nickname;
    private Integer singleScore;


}