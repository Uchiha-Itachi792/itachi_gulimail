package com.atguigu.gulimall.gulimallproduct.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AttrRespVo {
    /**
     * 			"catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;

    private Long attrGroupId;

    private static final long serialVersionUID = 1L;

    /**
     * 属性id

    /**
     * 属性名
     */
    private Long attrId;

    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;
}
