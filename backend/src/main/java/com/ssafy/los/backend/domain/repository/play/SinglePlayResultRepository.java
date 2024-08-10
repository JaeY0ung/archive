package com.ssafy.los.backend.domain.repository.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SinglePlayResultRepository extends JpaRepository<SinglePlayResult, Long> {

    @Query("SELECT s FROM SinglePlayResult s WHERE s.user = :user ORDER BY s.createdAt LIMIT 1")
    SinglePlayResult findByUserOrderCreatedAt(User user);
}
