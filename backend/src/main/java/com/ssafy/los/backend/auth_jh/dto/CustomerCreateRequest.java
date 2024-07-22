package com.ssafy.los.backend.auth_jh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerCreateRequest {

    private String nickname;
    private String password;

}
