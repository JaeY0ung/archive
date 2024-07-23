package com.ssafy.los.backend.user.model.repository;

import com.ssafy.los.backend.config.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
