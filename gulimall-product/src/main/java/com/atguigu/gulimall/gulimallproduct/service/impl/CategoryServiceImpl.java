package com.atguigu.gulimall.gulimallproduct.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.gulimallproduct.dao.CategoryDao;
import com.atguigu.gulimall.gulimallproduct.entity.CategoryEntity;
import com.atguigu.gulimall.gulimallproduct.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        //逆序转换
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }


    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid()!=0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;

    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> allList = categoryDao.getAll();
        List<CategoryEntity> listWithTree = new ArrayList<>();

        for (CategoryEntity categoryEntity : allList) {
            if (categoryEntity.getParentCid() == 0) {
                categoryEntity.setChildren(getChildren(categoryEntity, allList));
                listWithTree.add(categoryEntity);
            }
        }
        listWithTree.sort(Comparator.comparingInt(CategoryEntity::getSort));
        return listWithTree;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> allList) {
        List<CategoryEntity> childerens = new ArrayList<>();
        for (CategoryEntity entity : allList) {
            if (root.getCatId().equals(entity.getParentCid())) {
                entity.setChildren(getChildren(entity, allList));
                childerens.add(entity);
            }
        }
        childerens.sort(Comparator.comparingInt(item -> (item.getSort() == null ? 0 : item.getSort())));
        return childerens;
    }

    @Override
    public void logicDeleteCategory(List<Long> catIds) {
        for (Long catId : catIds) {
            categoryDao.logicDeleteCategory(catId);
        }
    }
}