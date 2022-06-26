package com.atguigu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product"})
public class ItemMainApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
