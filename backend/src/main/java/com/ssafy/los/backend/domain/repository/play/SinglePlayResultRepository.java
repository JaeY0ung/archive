package com.ssafy.los.backend.domain.repository.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinglePlayResultRepository extends JpaRepository<SinglePlayResult, Long> {

//    @Query("SELECT s FROM SinglePlayResult s WHERE s.user = :user ORDER BY s.createdAt LIMIT 1")
//    SinglePlayResult findOrderCreatedAtByUser(User user);

    Optional<SinglePlayResult> findByUserOrderByCreatedAtDesc(User user);
}
