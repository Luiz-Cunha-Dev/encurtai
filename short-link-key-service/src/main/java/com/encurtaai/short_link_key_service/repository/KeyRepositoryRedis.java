package com.encurtaai.short_link_key_service.repository;

import com.encurtaai.short_link_key_service.model.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class KeyRepositoryRedis implements KeyRepository {
    private static final String KEY_SET = "short-link-key-set";
    private final RedisTemplate <String, String> redisTemplate;

    public void save(Key key) {
        redisTemplate.opsForSet().add(KEY_SET, key.getToken());
    }

    public boolean exists(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(KEY_SET, token));
    }
}
