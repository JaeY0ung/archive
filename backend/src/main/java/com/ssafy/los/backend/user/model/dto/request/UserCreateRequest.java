package com.ssafy.los.backend.user.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserCreateRequest {

    private String username;
    private String password;

}
