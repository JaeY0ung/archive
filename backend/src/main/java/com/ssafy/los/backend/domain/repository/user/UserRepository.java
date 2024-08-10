package com.ssafy.los.backend.domain.repository.user;

import com.ssafy.los.backend.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndDeletedAtNull(String email);

    boolean existsByNicknameAndDeletedAtNull(String nickname);

    Optional<User> findByEmailAndDeletedAtNull(String email);

    Optional<User> findUserByIdAndDeletedAtNull(Long id);

    Optional<User> findByNicknameAndDeletedAtNull(String nickname);

}