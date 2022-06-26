package com.atguigu.gmall.front;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-25
 */

@SpringCloudApplication
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
public class FrontMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontMainApplication.class,args);
    }
}
