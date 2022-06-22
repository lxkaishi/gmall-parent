package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lxstart
 * @create 2022-06-21 20:53
 */
@ToString
@Slf4j
@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    @Autowired
    BaseAttrValueService baseAttrValueService;

    @GetMapping("/attrInfoList/{c1id}/{c2id}/{c3id}")
    public Result attrInfoList(@PathVariable("c1id") Long c1Id,
                               @PathVariable("c2id") Long c2Id,
                               @PathVariable("c3id") Long c3Id) {
        List<BaseAttrInfo> infos = baseAttrInfoService.getBaseAttrInfoWithValue(c1Id, c2Id, c3Id);

        return Result.ok(infos);
    }

    /**
     * 保存/修改平台属性二合一
     *
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        log.info("保存/修改数据: {}",baseAttrInfo);

        if (baseAttrInfo.getId() != null){
            //修改平台属性名和值
            baseAttrInfoService.updateAttrInfo(baseAttrInfo);
        }else {
            //保存
            baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        }
        return Result.ok();
    }

    /**
     * 查询某个属性的属性值
     *
     * @param attrInfoId
     * @return
     */
    @GetMapping("/getAttrValueList/{attrInfoId}")
    public Result getAttrValueList(@PathVariable("attrInfoId") Long attrInfoId) {

        List<BaseAttrValue> attrValueList = baseAttrValueService.getAttrValueList(attrInfoId);

        return Result.ok(attrValueList);
    }
}
