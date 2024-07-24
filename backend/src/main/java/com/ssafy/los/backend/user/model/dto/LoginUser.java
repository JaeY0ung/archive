package com.ssafy.los.backend.user.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginUser {

    private Long id;
    private String email;

}
