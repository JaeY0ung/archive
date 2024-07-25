package com.ssafy.los.backend.play.model.repository;

import com.ssafy.los.backend.play.model.entity.MultiPlayResult;
import java.util.List;

import com.ssafy.los.backend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MultiPlayResultRepository extends JpaRepository<MultiPlayResult, Long> {

    List<MultiPlayResult> findAllByWinner(User user);
    List<MultiPlayResult> findAllByLoser(User user);

    @Query("SELECT m FROM MultiPlayResult m WHERE m.winner =:user OR m.loser = :user ORDER BY m.createdAt")
    List<MultiPlayResult> findAllByUserOrderByCreatedAt(@Param("user") User user);

}
