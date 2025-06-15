package com.atguigu.gulimall.gulimallproduct.service;

import com.atguigu.common.to.product.Catalog2VO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.gulimallproduct.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-08 15:50:01
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();
    void logicDeleteCategory(List<Long> catIds);

    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2VO>> getCatalogJsonWithSpringCache();
}

