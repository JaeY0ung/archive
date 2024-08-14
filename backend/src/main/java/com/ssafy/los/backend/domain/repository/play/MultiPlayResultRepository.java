package com.ssafy.los.backend.domain.repository.play;

import com.ssafy.los.backend.domain.entity.MultiPlayResult;
import com.ssafy.los.backend.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MultiPlayResultRepository extends JpaRepository<MultiPlayResult, Long>,
        CustomMultiPlayRepository {

    List<MultiPlayResult> findAllByWinner(User user);

    List<MultiPlayResult> findAllByLoser(User user);

    @Query("SELECT m FROM MultiPlayResult m WHERE m.winner =:user OR m.loser = :user ORDER BY m.createdAt")
    List<MultiPlayResult> findAllByUserOrderByCreatedAt(@Param("user") User user);

}
