package com.yin.client.SDK;


import com.yin.client.SDK.client.YinApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * YuApi 客户端配置
 *
 */
@Configuration
@ConfigurationProperties("yuapi.client")//读取application.yml的配置，把读到的配置设置到属性中
@Data
@ComponentScan//扫包
public class YinApiClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public YinApiClient yuApiClient() {
        return new YinApiClient(accessKey, secretKey);
    }

}
