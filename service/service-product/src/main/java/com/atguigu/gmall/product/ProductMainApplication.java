package com.atguigu.gmall.product;

import com.atguigu.gmall.common.annotation.EnableAutoHandleException;
import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lxstart
 * @create 2022-06-20 21:15
 */
@EnableAutoHandleException
@Import(Swagger2Config.class)
@EnableTransactionManagement  //开启基于注解的自动事务管理
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.search")
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@SpringCloudApplication
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
