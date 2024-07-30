package com.ssafy.los.backend.user.model.repository;

import com.ssafy.los.backend.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findUserById(Long id);

//    Optional<User> findUserByToken(String token);

}
