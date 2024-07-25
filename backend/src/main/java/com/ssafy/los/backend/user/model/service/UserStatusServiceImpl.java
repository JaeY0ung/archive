package com.ssafy.los.backend.user.model.service;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStatusServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(UserStatusService.class.getName());
    private static final String USER_ONLINE_KEY_PREFIX = "USER_ONLINE_";
    private final RedisTemplate<String, Object> redisTemplate;

//    public UserStatusServiceImpl(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }

    public void setUserOnline(Long id) {
        redisTemplate.opsForValue().set("USER_ONLINE_" + id, true);
        LOGGER.info("User " + id + " set to online.");
    }

    public void setUserOffline(Long id) {
        redisTemplate.delete("USER_ONLINE_" + id);
        LOGGER.info("User " + id + " set to offline.");
    }

    public boolean isUserOnline(Long id) {
        Boolean online = (Boolean) redisTemplate.opsForValue().get("USER_ONLINE_" + id);
        LOGGER.info("User " + id + " is online: " + (online != null && online));
        return online != null && online;
    }

    public Set<String> getOnlineUsers() {
        // Redis에 저장된 모든 키 중에서 "USER_ONLINE_"로 시작하는 키를 검색하여 Set<String>으로 반환
        Set<String> keys = redisTemplate.keys("USER_ONLINE_*");
        LOGGER.info("Keys found: " + keys);
        return keys.stream()
                .filter(key -> {
                    Boolean online = (Boolean) redisTemplate.opsForValue().get(key);
                    LOGGER.info("Key: " + key + ", Online: " + online);
                    return online != null && online;
                })
                ///각 키에서 "USER_ONLINE_" 부분을 제거하고 사용자 ID만 남기도록 변환
                .map(key -> key.replace("USER_ONLINE_", ""))
                .collect(Collectors.toSet());
    }
}
