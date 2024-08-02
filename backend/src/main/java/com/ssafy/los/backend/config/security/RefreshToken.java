package com.ssafy.los.backend.config.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@RedisHash(value = "refreshToken", timeToLive = 1444000)
public class RefreshToken {

    @Id
    private String refreshToken;
    private Long userId;

    @Value("${refreshToken.timeToLive}")
    private long timeToLive;

    public RefreshToken(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
