package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.CustomOauth2User;
import com.ssafy.los.backend.user.model.dto.NaverResponse;
import com.ssafy.los.backend.user.model.dto.Oauth2Response;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청받은 oAuthUser 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("찾은 OAuth2 user = {}", oAuth2User.toString());

        // 출처마다 다른 응답 제작하기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
//            oAuth2Response = new GoogleReponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // OAuth2 로그인 유저 저장 및 업데이트 하기
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        log.info("Oauth2에서 저장할 username입니다. = {}", username);
        Optional<User> findUser = userRepository.findUserByToken(username);
        String role = null;
        if (findUser.isEmpty()) {
            User newUser = User.builder()
                    .token(username)
                    .email(oAuth2Response.getEmail())
                    .role("ROLE_USER")
                    .build();
            userRepository.save(newUser);
        } else {
            role = findUser.get().getRole();
            findUser.get().updateEmail(oAuth2Response.getEmail());
            userRepository.save(findUser.get());
        }

        // AuthenticateManager에게 제공?
        return new CustomOauth2User(oAuth2Response, role);


    }
}
