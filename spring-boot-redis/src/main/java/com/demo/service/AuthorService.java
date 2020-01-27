package com.demo.service;

import com.demo.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "author")
public class AuthorService {

    @Autowired
    private RedisTemplate<String, Author> authorRedisTemplate;

    private static final String REDIS_PREFIX_SESSIONS = "author";
    private static final String REDIS_KEYS_SEPARATOR = ":";

    @CachePut(key = "#author.id")
    public void save(final Author author) {
        author.setId(UUID.randomUUID().toString());
        authorRedisTemplate.opsForValue().set(getRedisKey(author.getId()), author);
    }

    @Cacheable
    public List<Author> findAll() {
        return authorRedisTemplate.opsForValue().multiGet(authorRedisTemplate.keys(getRedisKey("*")));
    }

    private String getRedisKey(final String key) {
        return REDIS_PREFIX_SESSIONS + REDIS_KEYS_SEPARATOR + key;
    }
}
