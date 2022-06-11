package com.example.commerce;

import com.example.commerce.conf.DataSourceProxyAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 商品服务入口
 * mysql, redis, nacos, kafka, zipkin
 * <a href="http://localhost:8001/commerce-goods-service/doc.html">swagger 文档</a>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@Import(DataSourceProxyAutoConfig.class)
public class GoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }
}
