package com.zhy.springbootshirorsa.redis.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


/**
 * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是
 * 通过Spring提供的Serializer序列化到数据库的。RedisTemplate默认使用的
 * 是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer
 * @author Dell
 *https://www.imooc.com/video/16965
 */
@Component
public class RedisUtil  {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
	
    
	
	@SuppressWarnings("unchecked")
	public void del(String key) {
		if(key.length() > 0 && key != null) {
			redisTemplate.delete(CollectionUtils.arrayToList(key));
		}
		
	}

	public Set<String> getKeys(String kString) {
		Set<String> sets = redisTemplate.keys(kString);
		return	sets;
	}

	public byte[] get(String key) {
		byte[] object = (byte[]) redisTemplate.opsForValue().get(key);
		return key == null ? null : object;
	}

	public boolean set(String key, byte[] value) {
		try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
		 return false;
	}
	
	public boolean expire(String key, int time) {
		try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
		
	}

	public boolean set(String key, byte[] value, int time) {
		try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

	public boolean set(String key, Object value, int index) {
		try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
	
	
}
