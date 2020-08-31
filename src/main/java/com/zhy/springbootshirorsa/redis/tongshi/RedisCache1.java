package com.zhy.springbootshirorsa.redis.tongshi;
import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @autor cwm
 * @date 2020/2/25
 * @desc 实现redis缓存
 **/
@Component
public class RedisCache1<K,V> implements Cache<K,V> {

    @Autowired
    RedisUtil1 redisUtil;
    @Value("${expire}")
    private Integer expire;

    private final String SHIRO_CACHE_PREFIX="shiro-cache:";

    private  String  getkey(K key){
        return SHIRO_CACHE_PREFIX+key;
    }
    @Override
    public V get(K k) throws CacheException {
        System.out.println("从redis中获取");
        Object o = redisUtil.get(getkey(k));
        return (V)o;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        System.out.println(v);
        boolean set = redisUtil.set(getkey(k), v,600);
        if(set){
            return v;
        }
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        Object o = redisUtil.get(getkey(k));
        redisUtil.del(getkey(k));
        return (V)o;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
