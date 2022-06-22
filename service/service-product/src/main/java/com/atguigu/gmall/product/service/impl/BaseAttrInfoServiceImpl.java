package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueService baseAttrValueService;

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoWithValue(Long c1Id, Long c2Id, Long c3Id) {
        return baseAttrInfoMapper.getBaseAttrInfoWithValue(c1Id, c2Id, c3Id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //添加属性名
        baseAttrInfoMapper.insert(baseAttrInfo);
        Long infoId = baseAttrInfo.getId();

        // 添加属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue attrValue : attrValueList) {
            attrValue.setAttrId(infoId);
        }
        baseAttrValueService.saveBatch(attrValueList);
    }

    @Override
    public void updateAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 1.修改属性名
        baseAttrInfoMapper.updateById(baseAttrInfo);

        // 2.修改属性值
        ArrayList<Long> ids = new ArrayList<>();
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue attrValue : attrValueList) {

            // 2.1新增属性值
            if (attrValue.getId() == null) {
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueService.save(attrValue);
            }

            // 2.2修改属性值
            if (attrValue.getId() != null) {
                baseAttrValueService.updateById(attrValue);
                ids.add(attrValue.getId());
            }
        }
        //2.3、删除(前端没带的值id，就是删除)
        //1、查出12原来是 59,60,61
        //2、前端带的id   60,61
        //3、计算差集：  59
        // delete * from base_attr_value
        // where attr_id=12 and id not in(60,61)
        if (ids.size() > 0){
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", baseAttrInfo.getId());
            wrapper.notIn("id", ids);
            baseAttrValueService.remove(wrapper);
        }else {
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", baseAttrInfo.getId());
            baseAttrValueService.remove(wrapper);
        }
    }


}




