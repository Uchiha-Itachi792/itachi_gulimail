package com.atguigu.gulimall.gulimallmember;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient //将gulimall-member注册进nacos中
@EnableFeignClients(basePackages = "com.atguigu.gulimall.gulimallmember.feign") //开启远程调用服务，并且扫描目标包下的类
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
