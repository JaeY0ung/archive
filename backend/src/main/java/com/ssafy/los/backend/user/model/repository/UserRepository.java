package com.ssafy.los.backend.user.model.repository;

import com.ssafy.los.backend.user.model.dto.request.UserMyPageDto;
import com.ssafy.los.backend.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<UserMyPageDto> findUserMyPageDtoById(Long id);

    Optional<User> findUserById(Long id);
}
