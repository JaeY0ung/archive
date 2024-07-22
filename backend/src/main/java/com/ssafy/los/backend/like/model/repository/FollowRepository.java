package com.ssafy.los.backend.like.model.repository;

import com.ssafy.los.backend.like.model.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

}
