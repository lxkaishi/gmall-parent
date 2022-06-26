package com.atguigu.gmall.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-25
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }

}
