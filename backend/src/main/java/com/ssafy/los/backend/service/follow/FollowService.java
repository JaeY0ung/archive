package com.ssafy.los.backend.service.follow;

import com.ssafy.los.backend.dto.FollowSimpleListDto;
import java.util.List;

public interface FollowService {

    void followUser(Long followerId, Long followedId);

    void unfollowUSer(Long followerId, Long follwedId);

    List<FollowSimpleListDto> getFollowingList(String nickname);

    List<FollowSimpleListDto> getFollowerList(String nickname);
}
