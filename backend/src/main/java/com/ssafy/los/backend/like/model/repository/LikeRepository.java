package com.ssafy.los.backend.like.model.repository;

import com.ssafy.los.backend.like.model.entity.LikeSheet;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<LikeSheet, Long> {

    Optional<LikeSheet> findByUserAndSheet(User user, Sheet sheet);

//    void deleteByUserAndSheet(User user, Sheet sheet);

    boolean existsByUserAndSheet(User user, Sheet sheet);

    List<LikeSheet> findBySheet(Sheet sheet);
    
    Optional<LikeSheet> deleteByUserEqualsAndSheetEquals(User user, Sheet sheet);

    Optional<LikeSheet> findByUserEqualsAndSheetEquals(User user, Sheet sheet);
}
