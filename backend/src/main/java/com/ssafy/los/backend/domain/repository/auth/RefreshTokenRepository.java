package com.ssafy.los.backend.domain.repository.auth;

import com.ssafy.los.backend.config.security.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
