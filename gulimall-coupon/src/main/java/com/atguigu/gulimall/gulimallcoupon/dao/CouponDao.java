package com.atguigu.gulimall.gulimallcoupon.dao;

import com.atguigu.gulimall.gulimallcoupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-16 15:01:51
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
