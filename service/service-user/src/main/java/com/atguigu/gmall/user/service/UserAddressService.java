package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author xfyy
* @description 针对表【user_address(用户地址表)】的数据库操作Service
* @createDate 2022-07-08 20:47:42
*/
public interface UserAddressService extends IService<UserAddress> {

    List<UserAddress> getUserAddress();
}
