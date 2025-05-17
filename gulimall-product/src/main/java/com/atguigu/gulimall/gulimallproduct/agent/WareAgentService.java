package com.atguigu.gulimall.gulimallproduct.agent;


import com.atguigu.common.to.ware.SkuHasStockTO;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallproduct.feign.WareFeignService;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WareAgentService {

    @Autowired
    WareFeignService wareFeignService;

    /**
     * 查询sku是否有库存
     */
    public List<SkuHasStockTO> getSkusHasStock(List<Long> skuIds) {
        R r = wareFeignService.getSkusHasStock(skuIds);
        // 解析结果
        TypeReference<List<SkuHasStockTO>> typeReference = new TypeReference<List<SkuHasStockTO>>() {
        };
        List<SkuHasStockTO> result = r.getData(typeReference);
        return result;
    }
}
