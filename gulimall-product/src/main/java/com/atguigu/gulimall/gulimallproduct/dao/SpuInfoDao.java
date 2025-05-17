package com.atguigu.gulimall.gulimallproduct.dao;

import com.atguigu.gulimall.gulimallproduct.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-02 21:20:40
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改商品状态
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
