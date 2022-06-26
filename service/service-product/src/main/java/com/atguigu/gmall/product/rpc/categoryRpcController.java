package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RPC暴露所有和分类有关的远程接口
 * 1、所有远程调用请求  /rpc/inner/product/调用路径
 *  /rpc/inner/微服务名/路径
 */
@RequestMapping("/rpc/inner/product")
@RestController
public class categoryRpcController {

    @Autowired
    CategoryBizService categoryBizService;

    @GetMapping("/categorys/all")
    public Result<List<CategoryVo>> getCategorys(){
        List<CategoryVo> vos = categoryBizService.getCategorys();
        return Result.ok(vos);
    }
}
