package com.atguigu.gulimall.gulimallproduct.service;

import com.atguigu.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.gulimallproduct.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-02 21:20:40
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询sku图片信息
     */
    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

