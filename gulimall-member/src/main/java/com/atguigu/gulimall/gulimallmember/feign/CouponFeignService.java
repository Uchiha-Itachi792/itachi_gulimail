package com.atguigu.gulimall.gulimallmember.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon") //gulimall-coupon为远程server的名称
//@Service
public interface CouponFeignService {
    @RequestMapping("gulimallcoupon/coupon/member/list") //此处需要全路径
    public R getCouponsByMemberId();
}
