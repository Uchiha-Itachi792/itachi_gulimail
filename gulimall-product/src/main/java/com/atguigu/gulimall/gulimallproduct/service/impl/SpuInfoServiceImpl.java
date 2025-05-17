package com.atguigu.gulimall.gulimallproduct.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.to.ware.SkuHasStockTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallproduct.agent.SearchAgentService;
import com.atguigu.gulimall.gulimallproduct.agent.WareAgentService;
import com.atguigu.gulimall.gulimallproduct.entity.*;
import com.atguigu.gulimall.gulimallproduct.feign.CouponFeignService;
import com.atguigu.gulimall.gulimallproduct.service.*;
import com.atguigu.gulimall.gulimallproduct.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.gulimallproduct.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    WareAgentService wareAgentService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SearchAgentService searchAgentService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        //对于mybatis-plus而言。将实体插入到DB之后，自动生成的ID会回写
        this.saveBaseSpuInfo(infoEntity);

        //2、保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        //使用回写的ID
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);


        //3、保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(), images);


        //4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);


        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }


        //5、保存当前spu对应的所有sku信息；

        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1）、sku的基本信息；pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2）、sku的图片信息；pms_sku_image
                if(!CollectionUtils.isEmpty(imagesEntities)) {
                    skuImagesService.saveBatch(imagesEntities);
                }
                //TODO 没有图片路径的无需保存

                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }
   @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        /**
         * status: 2
         * key:
         * brandId: 9
         * catelogId: 225
         */

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //一个spu旗下有很多的sku，因此需要一个list来存储sku
        // 1.查询spuId对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

        //2.查询spuId对应的所有属性AttrValue
        Map<Long, ProductAttrValueEntity> attrMap = productAttrValueService.baseAttrlistforspu(spuId)
                .stream().collect(Collectors.toMap(key -> key.getAttrId(), val -> val));

        //3.不是所有属性值都是合法的，因此需要获取可以被检索的属性ID(search_type = 1)
        List<Long> searAttrIds = attrService.selectSearchAttrIds(new ArrayList<>(attrMap.keySet()));

        //4.将查询允许被检索的基本属性属性封装成attrEsModels
        List<SkuEsModel.Attrs> attrEsModels = searAttrIds.stream().map(attrId -> {
            SkuEsModel.Attrs attrModel = new SkuEsModel.Attrs();
            ProductAttrValueEntity attrValue = attrMap.get(attrId);
            // 封装基本属性
            attrModel.setAttrId(attrValue.getAttrId());
            attrModel.setAttrName(attrValue.getAttrName());
            attrModel.setAttrValue(attrValue.getAttrValue());
            return attrModel;
        }).collect(Collectors.toList());

        //5.远程wareAgentService，查询是否有库存
        Map<Long, Boolean> skuHasStockMap = null;
        try {
            // 查询库存
            List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            skuHasStockMap = wareAgentService.getSkusHasStock(skuIds).stream()
                    .collect(Collectors.toMap(SkuHasStockTO::getSkuId, SkuHasStockTO::getHasStock));
        } catch (Exception e) {
            log.error("库存查询异常：原因{}", e);
        }

        //6.封装成ES model
        Map<Long, Boolean> finalSkuHasStockMap = skuHasStockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            // 3.遍历sku封装SkuEsModel
            SkuEsModel esModel = new SkuEsModel();
            // 封装属性名相同的属性值
            BeanUtils.copyProperties(sku, esModel);
            // 封装属性名不相同的属性值 skuPrice, skuImg
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            // 封装库存（远程调用失败，默认设置有库存）
            esModel.setHasStock(finalSkuHasStockMap == null || finalSkuHasStockMap.get(sku.getSkuId()));

            // TODO 热度评分 hotScore（这里直接设置0，待扩展）
            esModel.setHotScore(0L);

            // 封装品牌和分类的名字
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogName(category.getName());

            // 封装允许被检索的基本属性值（多条sku冗余）
            esModel.setAttrs(attrEsModels);
            return esModel;
        }).collect(Collectors.toList());

        // 4.调用es模块保存数据
        boolean result = searchAgentService.productStatusUp(upProducts);
        if (result) {
            // 上架成功
            // 5.修改sku商品状态，上架状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.PublishStatusEnum.SPU_UP.getCode());
        } else {
            // 上架失败
            // TODO 7.重复调用，接口幂等性（重试机制）

        }



    }
}