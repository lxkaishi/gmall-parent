package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xfyy
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-07-08 20:47:42
*/
public interface UserInfoService extends IService<UserInfo> {

    LoginSuccessRespVo login(UserInfo userInfo, String ipAddress);

    void logout(String token);
}
