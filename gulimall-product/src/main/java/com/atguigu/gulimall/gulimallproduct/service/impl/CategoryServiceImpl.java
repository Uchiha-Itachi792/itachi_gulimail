package com.atguigu.gulimall.gulimallproduct.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.product.CategoryConstant;
import com.atguigu.common.to.product.Catalog2VO;
import com.atguigu.gulimall.gulimallproduct.service.CategoryBrandRelationService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;


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


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
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

    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //查询所有一级分类
    @Override
    @Cacheable(value = {"category"}, key = "'getLevel1Categorys'", sync = true)
    public List<CategoryEntity> getLevel1Categorys() {
        // 查询父id=0
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 查询三级分类并封装成Map返回
     * 使用SpringCache注解方式简化缓存设置
     * sync = true：加本地锁，解决缓存击穿问题
     */
    @Cacheable(value = {"category"}, key = "'getCatalogJson'", sync = true)
    @Override
    public Map<String, List<Catalog2VO>> getCatalogJsonWithSpringCache() {
        // 未命中缓存
        // 1.double check，占锁成功需要再次检查缓存（springcache使用本地锁）
        // 查询非空即返回
        String catlogJSON = redisTemplate.opsForValue().get("getCatalogJson");
        if (!StringUtils.isEmpty(catlogJSON)) {
            // 查询成功直接返回不需要查询DB
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
            return result;
        }

        // 2.查询所有分类，按照parentCid分组
        Map<Long, List<CategoryEntity>> categoryMap = baseMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(key -> key.getParentCid()));

        // 3.获取1级分类
        List<CategoryEntity> level1Categorys = categoryMap.get(0L);

        // 4.封装数据
        Map<String, List<Catalog2VO>> result = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), l1Category -> {
            // 5.查询2级分类，并封装成List<Catalog2VO>
            List<Catalog2VO> catalog2VOS = categoryMap.get(l1Category.getCatId())
                    .stream().map(l2Category -> {
                        // 7.查询3级分类，并封装成List<Catalog3VO>
                        List<Catalog2VO.Catalog3Vo> catalog3Vos = categoryMap.get(l2Category.getCatId())
                                .stream().map(l3Category -> {
                                    // 封装3级分类VO
                                    Catalog2VO.Catalog3Vo catalog3Vo = new Catalog2VO.Catalog3Vo(l2Category.getCatId().toString(), l3Category.getCatId().toString(), l3Category.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                        // 封装2级分类VO返回
                        Catalog2VO catalog2VO = new Catalog2VO(l1Category.getCatId().toString(), catalog3Vos, l2Category.getCatId().toString(), l2Category.getName());
                        return catalog2VO;
                    }).collect(Collectors.toList());
            return catalog2VOS;
        }));
        return result;
    }



    /**
     * 查询三级分类（redisson分布式锁版本）。此方法未使用，保留只是为了学习用
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDBWithRedissonLock() {
        // 1.抢占分布式锁，同时设置过期时间
        RLock lock = redisson.getLock(CategoryConstant.LOCK_KEY_CATALOG_JSON);
        lock.lock(30, TimeUnit.SECONDS);
        try {
            // 2.查询DB
            Map<String, List<Catalog2VO>> result = getCatalogJsonFromDB();
            return result;
        } finally {
            // 3.释放锁
            lock.unlock();
        }
    }

    /**
     * 查询三级分类（从数据源DB查询）。此方法未使用，保留只是为了学习用
     * 加入分布式锁版本代码，double check检查
     */
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDB() {
        // 1.double check，占锁成功需要再次检查缓存
        // 查询非空即返回
        String catlogJSON = redisTemplate.opsForValue().get("catlogJSON");
        if (!StringUtils.isEmpty(catlogJSON)) {
            // 查询成功直接返回不需要查询DB
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
            return result;
        }

        // 2.查询所有分类，按照parentCid分组
        Map<Long, List<CategoryEntity>> categoryMap = baseMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(key -> key.getParentCid()));

        // 3.获取1级分类
        List<CategoryEntity> level1Categorys = categoryMap.get(0L);

        // 4.封装数据
        Map<String, List<Catalog2VO>> result = level1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), l1Category -> {
            // 3.查询2级分类，并封装成List<Catalog2VO>
            List<Catalog2VO> catalog2VOS = categoryMap.get(l1Category.getCatId())
                    .stream().map(l2Category -> {
                        // 4.查询3级分类，并封装成List<Catalog3VO>
                        List<Catalog2VO.Catalog3Vo> catalog3Vos = categoryMap.get(l2Category.getCatId())
                                .stream().map(l3Category -> {
                                    // 封装3级分类VO
                                    Catalog2VO.Catalog3Vo catalog3Vo = new Catalog2VO.Catalog3Vo(l2Category.getCatId().toString(), l3Category.getCatId().toString(), l3Category.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                        // 封装2级分类VO返回
                        Catalog2VO catalog2VO = new Catalog2VO(l1Category.getCatId().toString(), catalog3Vos, l2Category.getCatId().toString(), l2Category.getName());
                        return catalog2VO;
                    }).collect(Collectors.toList());
            return catalog2VOS;
        }));

        // 5.结果集存入redis
        // 关注锁时序问题，存入redis代码块必须在同步快内执行
        redisTemplate.opsForValue().set(CategoryConstant.CACHE_KEY_CATALOG_JSON, JSONObject.toJSONString(result), 1, TimeUnit.DAYS);

        return result;
    }
}