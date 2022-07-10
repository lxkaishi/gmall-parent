package com.atguigu.gmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-07-08
 */
@SpringCloudApplication
@MapperScan("com.atguigu.gmall.user.mapper")
public class UserMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserMainApplication.class,args);
    }
}
