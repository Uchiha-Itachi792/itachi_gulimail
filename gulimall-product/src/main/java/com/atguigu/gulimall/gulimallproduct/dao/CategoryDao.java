package com.atguigu.gulimall.gulimallproduct.dao;

import com.atguigu.gulimall.gulimallproduct.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-08 15:50:01
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
