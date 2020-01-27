package com.demo.service;

import com.demo.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@CacheConfig(cacheNames = "review")
public class ReviewService {

    @Autowired
    private RedisTemplate<String, Review> reviewRedisTemplate;

    private static final String REDIS_PREFIX_SESSIONS = "review";
    private static final String REDIS_KEYS_SEPARATOR = ":";

    @CachePut(key = "#review.id")
    public void save(final Review review) {
        review.setId(UUID.randomUUID().toString());
        review.setTime(new Timestamp(System.currentTimeMillis()));
        reviewRedisTemplate.opsForValue().set(getRedisKey(review.getId()), review);
        reviewRedisTemplate.expire(getRedisKey(review.getId()), 60, TimeUnit.SECONDS);
    }

    @Cacheable
    public List<Review> findAll() {
        return reviewRedisTemplate.opsForValue().multiGet(reviewRedisTemplate.keys(getRedisKey("*")));
    }

    private String getRedisKey(final String key) {
        return REDIS_PREFIX_SESSIONS + REDIS_KEYS_SEPARATOR + key;
    }
}
