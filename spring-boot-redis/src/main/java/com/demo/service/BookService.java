package com.demo.service;

import com.demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "book")
public class BookService {

    @Autowired
    private RedisTemplate<String, Book> bookRedisTemplate;

    private static final String REDIS_PREFIX_SESSIONS = "book";
    private static final String REDIS_KEYS_SEPARATOR = ":";

    @CachePut(key = "#book.id")
    public void save(final Book book) {
        book.setId(UUID.randomUUID().toString());
        bookRedisTemplate.opsForValue().set(getRedisKey(book.getId()), book);
    }

    @Cacheable
    public List<Book> findAll() {
        return bookRedisTemplate.opsForValue().multiGet(bookRedisTemplate.keys(getRedisKey("*")));
    }

    private String getRedisKey(final String key) {
        return REDIS_PREFIX_SESSIONS + REDIS_KEYS_SEPARATOR + key;
    }
}
