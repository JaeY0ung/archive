package com.ssafy.los.backend.user.model.repository;


import com.ssafy.los.backend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<User, Long> {

}
