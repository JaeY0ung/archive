package com.ssafy.los.backend.like.model.repository;

import com.ssafy.los.backend.like.model.entity.LikeSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<LikeSheet, Long> {

}
