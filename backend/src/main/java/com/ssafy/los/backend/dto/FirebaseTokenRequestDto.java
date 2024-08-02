package com.ssafy.los.backend.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FirebaseTokenRequestDto {

    private Long userId;
    private String firebaseToken;
}