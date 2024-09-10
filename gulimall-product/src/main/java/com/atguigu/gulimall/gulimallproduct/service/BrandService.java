package com.atguigu.gulimall.gulimallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.gulimallproduct.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-08 15:50:01
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

