package com.project.notification.service;

import com.project.notification.response.NotificationProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationProfileCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX =
            "notification-profile:";

    private static final Duration CACHE_TTL =
            Duration.ofMinutes(30);

    private String getKey(Long userId) {
        return CACHE_PREFIX + userId;
    }

    public void save(
            NotificationProfileResponse profile) {

        redisTemplate.opsForValue().set(
                getKey(profile.getUserId()),
                profile,
                CACHE_TTL
        );
    }
    public NotificationProfileResponse get(Long userId) {

        Object value =
                redisTemplate.opsForValue().get(
                        getKey(userId)
                );

        if (value == null) {
            return null;
        }

        return (NotificationProfileResponse) value;
    }

    public void delete(Long userId) {

        redisTemplate.delete(
                getKey(userId)
        );
    }
}
