package com.ssafy.los.backend.user.model.dto;

public interface Oauth2Response {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();

}
