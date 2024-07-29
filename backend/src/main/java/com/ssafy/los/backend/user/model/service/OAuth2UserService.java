package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.CustomOAuth2User;
import com.ssafy.los.backend.user.model.dto.NaverResponse;
import com.ssafy.los.backend.user.model.dto.OAuth2Response;
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
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청받은 oAuthUser 가져오기 및 응답 만들기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("클라이언트가 접근한 OAuth2 user입니다. = {}", oAuth2User.toString());
        OAuth2Response oAuth2Response = getOAuth2Response(userRequest, oAuth2User);

        // 유저 정보 처리하기
        String email = oAuth2Response.getEmail();
        log.info("OAuth2에서 받은 email입니다. = {}", email);
        Optional<User> findUser = userRepository.findByEmail(email);

        // 해당 이메일이 있는 경우 -> 로그인 진행하기
        if (findUser.isPresent()) {
            log.info("해당 이메일로 가입된 계정이 있습니다. = {}", oAuth2Response.getEmail());
            return new CustomOAuth2User(findUser.get()); // Provider에게 제공
        } else {
            // 해당 이메일이 없는 경우 -> 회원가입 진행하기
            log.info("해당 이메일로 가입된 계정이 있습니다. = {}", oAuth2Response.getEmail());
            User newUser = User.builder()
                    // TODO : 추가 정보 있으면 넘기기
                    .email(oAuth2Response.getEmail())
//                    .role("ROLE_TEMP") // 없으면 회원가입 필요
                    .build();
            userRepository.save(newUser); // 이후에 가입으로 추가 업데이트 하기
            return new CustomOAuth2User(newUser); 
        }
    }

    private OAuth2Response getOAuth2Response(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (registrationId.equals("naver")) {
            return new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            // return new GoogleResponse(oAuth2User.getAttributes());
        }
        throw new OAuth2AuthenticationException("Unsupported OAuth2 service");
    }
}
