package com.zhy.springbootshirorsa.redis.tongshi;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @autor cwm
 * @date 2020/2/25
 * @desc redis缓存数据
 **/
public class RedisCacheManager1 implements CacheManager {
    @Autowired
    RedisCache1 redisCache;
    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return redisCache;
    }
}
