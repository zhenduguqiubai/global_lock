package com.bjpowernode.distributed.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissionConfig {
    @Value("127.0.0.1")
    private  String host;
    @Value("6379")
    private  String port;
    @Value("123456mg")
    private  String password;
@Bean
    public RedissonClient redissonClient(){
    Config config = new Config();
    config.useSingleServer().setAddress("redis://"+ host+":"+port);
    return Redisson.create(config);


}

}
