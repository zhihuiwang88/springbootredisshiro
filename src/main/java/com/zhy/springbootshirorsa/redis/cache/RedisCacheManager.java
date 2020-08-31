package com.zhy.springbootshirorsa.redis.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisCacheManager implements CacheManager{

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisCache redisCache;
	
	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Cache<K, V> getCache(String str) throws CacheException {
		 return  (Cache<K, V>) redisCache;
	}

}
