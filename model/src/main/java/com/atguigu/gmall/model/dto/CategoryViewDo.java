package com.atguigu.gmall.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@Data
@TableName("base_category_view")
public class CategoryViewDo {

    private Long id;

    private Long category1Id;
    private String category1Name;

    private Long category2Id;
    private String category2Name;

    private Long category3Id;
    private String category3Name;
}
