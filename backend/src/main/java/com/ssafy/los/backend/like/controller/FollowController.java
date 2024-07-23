package com.ssafy.los.backend.like.controller;

import com.ssafy.los.backend.like.model.dto.FollowSimpleListDto;
import com.ssafy.los.backend.like.model.service.FollowService;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.service.AuthService;
import com.ssafy.los.backend.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    private final FollowService followService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthService authService;

    // 유저 팔로우
    @PostMapping("/followings/{followed-id}")
    public ResponseEntity<?> followUser(@PathVariable("followed-id") Long followedId) {
        // 임시 로그인된 ID
        Long followerId = authService.getLoginUser().getId();
        followService.followUser(followerId, followedId);
        return new ResponseEntity<>(followedId, HttpStatus.CREATED);
    }

    // 유저 언팔로우
    @DeleteMapping("/followings/{followed-id}")
    public ResponseEntity<?> unfollowUser(@PathVariable("followed-id") Long followedId) {
        // 임시 로그인된 ID
        Long followerId = authService.getLoginUser().getId();
        followService.unfollowUSer(followerId, followedId);
        return new ResponseEntity<>(followedId, HttpStatus.OK);
    }

    // 팔로잉 조회
    @GetMapping("/followings")
    public ResponseEntity<?> getFollowingList() {
        Long loginId = authService.getLoginUser().getId();
        List<FollowSimpleListDto> followingList = followService.getFollowingList(loginId);

        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    // 팔로워 조회
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowerList() {
        Long loginId = authService.getLoginUser().getId();
        List<FollowSimpleListDto> followerList = followService.getFollowerList(loginId);

        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }

//    // 해당 유저의 팔로워 수 조회
//    @GetMapping("/follower/{user-id}/count")
//    public ResponseEntity<?> getFollowerCount(@PathVariable("user-id") Long userId) {
//        Long count = followService.countByFollowed(userId);
//        return new ResponseEntity<>(count, HttpStatus.OK);
//    }
}
