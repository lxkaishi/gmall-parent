package com.atguigu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@EnableAspectJAutoProxy
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product"})
public class ItemMainApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
