package com.atguigu.gulimall.gulimallproduct.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallproduct.agent.CouponAgentService;
import com.atguigu.gulimall.gulimallproduct.entity.SkuImagesEntity;
import com.atguigu.gulimall.gulimallproduct.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.gulimallproduct.vo.SeckillSkuVO;
import com.atguigu.gulimall.gulimallproduct.vo.SkuItemSaleAttrVO;
import com.atguigu.gulimall.gulimallproduct.vo.SkuItemVO;
import com.atguigu.gulimall.gulimallproduct.service.SkuImagesService;
import com.atguigu.gulimall.gulimallproduct.service.SkuSaleAttrValueService;
import com.atguigu.gulimall.gulimallproduct.service.SpuInfoDescService;
import com.atguigu.gulimall.gulimallproduct.vo.SpuItemAttrGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.gulimallproduct.dao.SkuInfoDao;
import com.atguigu.gulimall.gulimallproduct.entity.SkuInfoEntity;
import com.atguigu.gulimall.gulimallproduct.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponAgentService couponAgentService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupServiceImpl attrGroupService;
//    @Autowired
//    SeckillFeignService seckillFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {

            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);

                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVO result = new SkuItemVO();

        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            // 1.获取sku基本信息（pms_sku_info）【默认图片、标题、副标题、价格】
            SkuInfoEntity skuInfo = getById(skuId);
            result.setInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            // 2.获取sku图片信息（pms_sku_images）
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            result.setImages(images);
        }, executor);

//        CompletableFuture<Void> seckillSkuFuture = CompletableFuture.runAsync(() -> {
//            // 3.查询当前商品是否参与秒杀优惠
//            R r = seckillFeignService.getSkuSeckilInfo(skuId);
//            if (r.getCode() == 0) {
//                SeckillSkuVO seckillSku = r.getData(new TypeReference<SeckillSkuVO>() {
//                });
//                result.setSeckillSku(seckillSku);
//            }
//        }, executor);

        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            // 4.获取当前sku所属spu下的所有销售属性组合（pms_sku_info、pms_sku_sale_attr_value）
            List<SkuItemSaleAttrVO> saleAttr = skuSaleAttrValueService.getSaleAttrBySpuId(skuInfo.getSpuId());
            result.setSaleAttr(saleAttr);
        }, executor);

        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            // 5.获取spu商品介绍（pms_spu_info_desc）【描述图片】
            SpuInfoDescEntity desc = spuInfoDescService.getById(skuInfo.getSpuId());
            result.setDesc(desc);
        }, executor);

        CompletableFuture<Void> groupAttrsFuture = skuInfoFuture.thenAcceptAsync((skuInfo) -> {
            // 6.获取spu规格参数信息（pms_product_attr_value、pms_attr_attrgroup_relation、pms_attr_group）
            List<SpuItemAttrGroupVO> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId());
            result.setGroupAttrs(groupAttrs);
        }, executor);

        // 等待所有任务都完成
        //CompletableFuture.allOf(imagesFuture, saleAttrFuture, descFuture, groupAttrsFuture, seckillSkuFuture).get();
        CompletableFuture.allOf(imagesFuture, saleAttrFuture, descFuture, groupAttrsFuture).get();

        return result;
    }

}