package com.atguigu.gmall.item;

import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@EnableThreadPool
@EnableAspectJAutoProxy
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product","com.atguigu.gmall.feign.search"})
public class ItemMainApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
