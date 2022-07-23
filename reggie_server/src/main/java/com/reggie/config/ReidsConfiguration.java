package com.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class ReidsConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的key序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置redis的连接工程
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
