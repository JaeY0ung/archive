package com.ssafy.los.backend.domain.repository.follow;

import com.ssafy.los.backend.domain.entity.Follow;
import com.ssafy.los.backend.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 해당 유저가 이미 팔로우나 팔로잉을 했던 경우
    boolean existsByFollowerAndFollowed(User follower, User followed);

    // 유저들간의 팔로우를 취소하는 경우
    void deleteByFollowerAndFollowed(User follower, User followed);

    // 내가 팔로우한 관계를 찾아온다.
    List<Follow> findByFollowed(User user);

    // 나를 팔로우하는 관계를 찾아온다.
    List<Follow> findByFollower(User user);
}
