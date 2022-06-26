package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.model.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-25
 */
@Controller
public class IndexController {

    @Autowired
    CategoryFeignClient categoryFeignClient;

    @GetMapping("/")
    public String indexPage(Model model){

        Result<List<CategoryVo>> result = categoryFeignClient.lxlx();
        List<CategoryVo> categoryVoList = result.getData();
        model.addAttribute("list",categoryVoList);

        return "index/index";
    }
}
