package com.yin.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig{
    /**
     * 根据路径限流
     */
    @Primary
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getURI().getPath());
    }

    /**
     * 根据参数限流
     */
    @Bean
    public KeyResolver parameterKeyResolver() {
        return exchange ->
                Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
    }

    /**
     * 根据 IP 限流
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange ->
                Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }
}

