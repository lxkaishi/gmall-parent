package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-25
 */
@FeignClient("service-product")
@RequestMapping("/rpc/inner/product")

public interface CategoryFeignClient {

    @GetMapping("/categorys/all")
    Result<List<CategoryVo>> lxlx();

}
