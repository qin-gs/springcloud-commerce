package com.example.commerce.sampler;

import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import org.springframework.cloud.sleuth.sampler.ProbabilityBasedSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用配置的方式设置抽样率
 */
@Configuration
public class SamplerConfig {

    /**
     * 限速采集
     */
    @Bean
    public Sampler sampler() {
        return RateLimitingSampler.create(100);
    }

    /**
     * 抽样采集
     */
    // @Bean
    public Sampler defaultSampler() {
        return ProbabilityBasedSampler.create(0.5f);
    }
}
