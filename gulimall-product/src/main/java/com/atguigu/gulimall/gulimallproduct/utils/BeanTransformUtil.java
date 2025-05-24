package com.atguigu.gulimall.gulimallproduct.utils;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.gulimallproduct.entity.SkuInfoEntity;

public class BeanTransformUtil {

    public void SkuInfoEntity2SkuEsModel(SkuEsModel esModel, SkuInfoEntity skuInfoEntity) {
        esModel.setSkuPrice(skuInfoEntity.getPrice());
        //esModel.set

    }
}
