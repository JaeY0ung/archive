package com.ssafy.los.backend.user.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOauth2User implements OAuth2User {

    private final Oauth2Response oauth2Response;
    private final String role;


    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });
        return authorities;
    }

    @Override
    public String getName() {
        return oauth2Response.getName();
    }

    public String getUsername() {
        return oauth2Response.getProvider() + " " + oauth2Response.getProviderId();
    }
}
