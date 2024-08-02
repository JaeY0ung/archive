package com.ssafy.los.backend.dto.user;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();

}
