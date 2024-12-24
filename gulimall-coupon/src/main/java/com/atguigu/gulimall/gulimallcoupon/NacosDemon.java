package com.atguigu.gulimall.gulimallcoupon;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;

public class NacosDemon {

    public static void main(String[] args) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", "127.0.0.1:8848");
        //gulimall-coupon没有定义namespace，所有不用填写namespace的相关信息
        //properties.setProperty("namespace", "public");
        ConfigService configService = NacosFactory.createConfigService(properties);

        String dataId = "gulimall-coupon";
        String group = "DEFAULT_GROUP";
        long timeout = 1000l;
        String config = configService.getConfig(dataId, group, timeout);
        System.out.println("config is " + config);
    }

}
