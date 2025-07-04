package com.atguigu.gulimall.gulimallproduct.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.gulimallproduct.vo.SkuItemVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.gulimallproduct.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-02 21:20:40
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException;
}

