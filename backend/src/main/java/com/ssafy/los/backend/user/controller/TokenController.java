package com.ssafy.los.backend.user.controller;

import com.ssafy.los.backend.user.model.repository.RefreshTokenRepository;
import com.ssafy.los.backend.user.model.service.AuthService;
import com.ssafy.los.backend.user.model.service.UserService;
import com.ssafy.los.backend.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {
    private final JWTUtil jwtUtil;

    /**
     * 새로운 accessToken 발급 -> 유저의 accessToken이 만료될 시 해당 클라이언트의 쿠키의 refreshToken으로 부터 검증 과정을 거친다.
     */
//    @GetMapping("/refresh")
//    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//        //get refresh token
//        String refresh = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//
//            if (cookie.getName().equals("refreshToken")) {
//
//                refresh = cookie.getValue();
////                System.out.println(refresh); // 정상확인
//            }
//        }
//        // 요청이 들어왔을 때, client의 cookie에 refreshToken이 없을 경우
//        if (refresh == null) {
//
//            //response status code
//            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
//        }
//
//        //expired check
//        try {
//            jwtUtil.isExpired(refresh);
//        } catch (ExpiredJwtException e) {
//
//            //response status code
//            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
//        String category = jwtUtil.getCategory(refresh);
//
//        if (!category.equals("refresh")) {
//
//            //response status code
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }


        // 가져온 정보로부터 유저 정보확인
//        String username = jwtUtil.getUsername(refresh);
//        String role = jwtUtil.getRole(refresh);
//
//        System.out.println("username = " + username);
//        System.out.println("role = " + role);
//
//        //make new JWT
////        String newAccessToken = jwtUtil.createJwt(username, role, 600000L);
//        String newAccessToken = jwtUtil.createJwt(username, role, 60L);
//
//        //response
//        response.setHeader("accessToken", newAccessToken);
//
//        return new ResponseEntity<>(newAccessToken, HttpStatus.OK);
//    }





}
