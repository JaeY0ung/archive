package com.ssafy.los.backend.controller.follow;

import com.ssafy.los.backend.dto.FollowSimpleListDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.follow.FollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;
    private final AuthService authService;

    // 유저 팔로우
    @PostMapping("/followings/{followed-id}")
    public ResponseEntity<?> followUser(@PathVariable("followed-id") Long followedId) {
        Long followerId = authService.getLoginUser().getId();
        followService.followUser(followerId, followedId);
        return new ResponseEntity<>(followedId, HttpStatus.CREATED);
    }

    // 유저 언팔로우
    @DeleteMapping("/followings/{followed-id}")
    public ResponseEntity<?> unfollowUser(@PathVariable("followed-id") Long followedId) {
        Long followerId = authService.getLoginUser().getId();
        followService.unfollowUSer(followerId, followedId);
        return new ResponseEntity<>(followedId, HttpStatus.OK);
    }

    // 팔로잉 조회
    @GetMapping("/followings/{nickname}")
    public ResponseEntity<?> getFollowingList(@PathVariable("nickname") String nickname) {
//        Long loginId = authService.getLoginUser().getId();
        List<FollowSimpleListDto> followingList = followService.getFollowingList(nickname);

        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    // 팔로워 조회
    @GetMapping("/followers/{nickname}")
    public ResponseEntity<?> getFollowerList(@PathVariable("nickname") String nickname) {
//        Long loginId = authService.getLoginUser().getId();
        List<FollowSimpleListDto> followerList = followService.getFollowerList(nickname);

        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }

}
