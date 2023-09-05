package com.yin.Interface.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redission配置
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
//TODO redission应该在需要使用分布式锁的项目模块配置