package com.ssafy.los.backend.user.model.dto;

public interface OAuth2Response {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();

}
