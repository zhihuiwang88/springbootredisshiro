package com.zhy.springbootshirorsa.redis.tongshi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @autor cwm
 * @date 2020/2/20
 * @desc redis配置类
 **/
@Configuration
public class RedisConfig1 {
    //GenericJackson2JsonRedisSerializer
    //Jackson2JsonRedisSerializer
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new MyRedisSerializer());
//        redisTemplate.setHashValueSerializer(new MyRedisSerializer());
//        redisTemplate.setDefaultSerializer(new MyRedisSerializer());

        redisTemplate.setValueSerializer(new MyRedisSerializer1());
        redisTemplate.setHashValueSerializer(new MyRedisSerializer1());
        redisTemplate.setDefaultSerializer(new MyRedisSerializer1());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
