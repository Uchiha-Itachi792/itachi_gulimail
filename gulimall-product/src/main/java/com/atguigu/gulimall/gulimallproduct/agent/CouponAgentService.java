package com.atguigu.gulimall.gulimallproduct.agent;

import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.product.SkuReductionTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallproduct.feign.CouponFeignService;
import com.atguigu.gulimall.gulimallproduct.vo.Bounds;
import com.atguigu.gulimall.gulimallproduct.vo.Skus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouponAgentService {

    @Autowired
    CouponFeignService couponFeignService;

    /**
     * 新增积分信息（当前spu商品购买新增的积分规则信息）
     */
    public R saveSpuBounds(Long spuId, Bounds bounds) {
        SpuBoundTo boundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, boundTo);
        boundTo.setSpuId(spuId);
        return couponFeignService.saveSpuBounds(boundTo);
    }

    /**
     * 新增满减信息
     */
    public R saveSkuReduction(Long skuId, Skus sku) {
        SkuReductionTo reductionTo = new SkuReductionTo();
        BeanUtils.copyProperties(sku, reductionTo);
        reductionTo.setSkuId(skuId);
        return couponFeignService.saveSkuReduction(reductionTo);
    }
}

