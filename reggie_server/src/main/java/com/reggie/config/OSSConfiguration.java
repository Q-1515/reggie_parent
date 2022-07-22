package com.reggie.config;

import com.reggie.properties.AliOSSProperties;
import com.reggie.utils.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对象存储配置类
 */
@Configuration
@Slf4j
public class OSSConfiguration {

    /**
     * 创建对象存储工具类
     *
     * @param aliOSSProperties 对象存储参数注入
     * @return 对象存储
     */
    @Bean
    @ConditionalOnMissingBean
    public AliOSSUtil aliOSSUtil(AliOSSProperties aliOSSProperties) {
        return AliOSSUtil.builder()
                .endpoint(aliOSSProperties.getEndpoint())
                .accessKeyId(aliOSSProperties.getAccessKeyId())
                .accessKeySecret(aliOSSProperties.getAccessKeySecret())
                .bucketName(aliOSSProperties.getBucketName())
                .build();
    }
}
