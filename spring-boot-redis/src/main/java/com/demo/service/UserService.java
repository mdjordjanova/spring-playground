package com.demo.service;

import com.demo.model.Review;
import com.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private RedisTemplate<String, User> userRedisTemplate;

    private static final String REDIS_PREFIX_SESSIONS = "user";
    private static final String REDIS_KEYS_SEPARATOR = ":";

    @CachePut(key = "#user.id")
    public void save(final User user) {
        user.setId(UUID.randomUUID().toString());
        userRedisTemplate.opsForValue().set(getRedisKey(user.getId()), user);
    }

    @Cacheable
    public List<User> findAll() {
        return userRedisTemplate.opsForValue().multiGet(userRedisTemplate.keys(getRedisKey("*")));
    }

    private String getRedisKey(final String key) {
        return REDIS_PREFIX_SESSIONS + REDIS_KEYS_SEPARATOR + key;
    }
}
