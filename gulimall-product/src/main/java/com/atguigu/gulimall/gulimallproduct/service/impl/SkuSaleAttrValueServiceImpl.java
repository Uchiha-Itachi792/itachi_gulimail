package com.atguigu.gulimall.gulimallproduct.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.gulimallproduct.vo.SkuItemSaleAttrVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.gulimallproduct.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.gulimallproduct.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimall.gulimallproduct.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取spu下的所有销售属性组合
     */
    @Override
    public List<SkuItemSaleAttrVO> getSaleAttrBySpuId(Long spuId) {
        // 1.通过spuId查询所有sku（pms_sku_info）
        // 2.查询sku涉及到的所有销售属性（pms_sku_sale_attr_value）
        return baseMapper.getSaleAttrBySpuId(spuId);
    }

    /**
     * 根据skuId查询销售属性值
     * @param skuId
     * @return skuId:skuValue
     */
    @Override
    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {
        return baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
    }

    /**
     * 根据skuId查询销售属性值
     * @param skuId
     * @return skuId:skuValue
     */
//    @Override
//    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {
//        return baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
//    }

}