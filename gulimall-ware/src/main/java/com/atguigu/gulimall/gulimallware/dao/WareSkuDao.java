package com.atguigu.gulimall.gulimallware.dao;

import com.atguigu.gulimall.gulimallware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-16 16:45:03
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkusStock(@Param("skuId") Long skuId);
}
