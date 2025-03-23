package com.atguigu.gulimall.gulimallproduct.service.impl;

import com.atguigu.common.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.gulimallproduct.dao.SpuImagesDao;
import com.atguigu.gulimall.gulimallproduct.entity.SpuImagesEntity;
import com.atguigu.gulimall.gulimallproduct.service.SpuImagesService;
import org.springframework.util.CollectionUtils;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long id, List<String> images) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }
        List<SpuImagesEntity> collect = images.stream().map(url -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(id);
            spuImagesEntity.setImgUrl(url);
            return spuImagesEntity;
        }).collect(Collectors.toList());

        this.saveBatch(collect);
    }

}