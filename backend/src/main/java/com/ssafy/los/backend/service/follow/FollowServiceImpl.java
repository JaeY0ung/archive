package com.ssafy.los.backend.service.follow;

import com.ssafy.los.backend.domain.entity.Follow;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.follow.FollowRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.FollowSimpleListDto;
import java.util.ArrayList;
import java.util.List;

import com.ssafy.los.backend.service.user.UserService;
import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FileUploadUtil fileUploadUtil;


    // 팔로우 추가하기
    @Override
    public void followUser(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 유저의 ID를 불러올 수 없습니다."));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("팔로우하려는 대상을 찾을 수 없습니다."));

        if (followRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new RuntimeException("이미 팔로우 관계가 되어있습니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .followed(followed)
                .build();

        followRepository.save(follow);
    }

    // 언팔로우 하기
    @Override
    public void unfollowUSer(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 유저의 ID를 불러올 수 없습니다."));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("언팔로우하려는 대상을 찾을 수 없습니다."));

        if (!followRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new RuntimeException("팔로우 관계가 아닙니다.");
        }

        followRepository.deleteByFollowerAndFollowed(follower, followed);
    }

    // 팔로잉 조회
    @Override
    public List<FollowSimpleListDto> getFollowingList(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        List<Follow> followingList = followRepository.findByFollower(user);

        List<FollowSimpleListDto> followSimpleListDtoList = new ArrayList<>();

        for (Follow follow : followingList) {
            // TODO: SELECT 쿼리 발생 (N + 1 쿼리 문제 발생 지점)
            User target = userRepository.findById(follow.getFollowed().getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
            followSimpleListDtoList.add(
                    new FollowSimpleListDto(target.getNickname(), target.getUserImg()));
        }
        return followSimpleListDtoList;
    }

    // 팔로워 조회
    @Override
    public List<FollowSimpleListDto> getFollowerList(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        List<Follow> followerList = followRepository.findByFollowed(user);

        List<FollowSimpleListDto> followerSimpleListDtoList = new ArrayList<>();

        for (Follow follow : followerList) {
            // TODO: SELECT 쿼리 발생 (N + 1 쿼리 문제 발생 지점)
            User target = userRepository.findById(follow.getFollower().getId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

            FollowSimpleListDto followSimpleListDto = new FollowSimpleListDto(target.getNickname(), target.getUserImg());
            followSimpleListDto.loadUserImg(fileUploadUtil);

            followerSimpleListDtoList.add(followSimpleListDto);
        }
        return followerSimpleListDtoList;
    }
}
