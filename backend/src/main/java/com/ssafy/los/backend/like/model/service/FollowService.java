package com.ssafy.los.backend.like.model.service;

import com.ssafy.los.backend.like.model.dto.FollowSimpleListDto;

import java.util.List;

public interface FollowService {

    void followUser(Long followerId, Long followedId);
    void unfollowUSer(Long followerId, Long follwedId);

    List<FollowSimpleListDto> getFollowingList(String nickname);
    List<FollowSimpleListDto> getFollowerList(String nickname);
}
