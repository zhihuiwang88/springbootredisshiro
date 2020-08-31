package com.zhy.springbootshirorsa.redis.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
public class RedisCache<K,V> implements Cache<K, V>{

	    @Autowired
	    RedisUtil redisUtil;
	    // 过期时间，单位：妙
	    private final  int EXPIRE_DATE = 600;
	    
	    @SuppressWarnings("unused")
		private final String CACHE_PREFIX = "redis-cache:";
	    
	    @SuppressWarnings("unused")
		private byte[] getKey(K k){
	        if(k instanceof String){
	            return (CACHE_PREFIX+k).getBytes();
	        }
	        // 序列化对象
	        return SerializationUtils.serialize(k);
	       }
	    
	/**
	 * 清空整个缓存 
	 */
	@Override
	public void clear() throws CacheException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 根据Key获取缓存中的值
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws CacheException {
		System.out.println("从redis中取数据");
		byte[] value  = redisUtil.get(getKey(key).toString());
		if (value != null) {
		return (V) SerializationUtils.deserialize(value);
		}
		return null;
	}

	
	/**
	 * 获取缓存中所有的key
	 */
	@Override
	public Set<K> keys() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 往缓存中放入key-value，返回缓存中之前的值
	 */
	@Override
	public V put(K k, V v) throws CacheException {
		byte[] key =  getKey(k);
		 boolean set = redisUtil.set(key.toString(), v,EXPIRE_DATE);
		 if (set) {
			 return v;
		}
		 return null;
	}

	/**
	 * 移除缓存中key对应的值，返回该值
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V remove(K k) throws CacheException {
		// 获取key和value
		byte[] key =  getKey(k);
        byte[] value = redisUtil.get(key.toString());
        redisUtil.del(key.toString());
        if(value != null ){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
	}

	/**
	 * 返回缓存大小
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/**
	 * 获取缓存中所有的value 
	 */
	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
