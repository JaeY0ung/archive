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
        // 요청받은 oAuthUser 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("클라이언트가 접근한 OAuth2 user = {}", oAuth2User.toString());

        // 가져온 oAuthUser 출처마다 다른 OAuth2Response 제작하기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
//            oAuth2Response = new GoogleReponse(oAuth2User.getAttributes());
        } else {
            log.info("지원하지 않는 OAuth2 서비스 입니다. = {}", registrationId);
            return null;
        }

        // OAuth2 로그인 유저 저장 및 업데이트 하기
        // TODO : 회원가입인 경우 새로운 로직 필요
        String email = oAuth2Response.getEmail();
        log.info("OAuth2에서 저장할 email입니다. = {}", email);
        Optional<User> findUser = userRepository.findByEmail(email);
        
        // 저장할 OAuth2 계정 이메일이 존재하는 경우
        if (findUser.isPresent()) {
            log.info("해당 이메일로 가입된 계정이 있습니다. = {}", findUser.get().getEmail());
            return new CustomOAuth2User(findUser.get());
        }
        // 새롭게 등록하는 경우
        User newUser = User.builder()
                .email(oAuth2Response.getEmail())
                .role("ROLE_USER")
                // TODO : 추가 정보 적기
                .build();
        userRepository.save(newUser);
        return new CustomOAuth2User(newUser); // Provider에게 제공
    }
}
