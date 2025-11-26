package com.xcache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public void store(String key, byte[] data) {
        redisTemplate.opsForValue().set(key, data);
        // Optional: set expiration (e.g., 30 days for build cache)
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public Optional<byte[]> retrieve(String key) {
        byte[] data = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(data);
    }
}