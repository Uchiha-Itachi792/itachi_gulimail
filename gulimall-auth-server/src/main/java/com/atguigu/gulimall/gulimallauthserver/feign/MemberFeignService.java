package com.atguigu.gulimall.gulimallauthserver.feign;

import com.atguigu.common.utils.R;
import com.atguigu.common.to.WBSocialUserTo;
import com.atguigu.gulimall.gulimallauthserver.vo.UserLoginVO;
import com.atguigu.gulimall.gulimallauthserver.vo.UserRegisterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 注册
     */
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegisterVO user);

    /**
     * 登录
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVO vo);

    /**
     * 微博社交登录
     */
    @PostMapping("/member/member/weibo/oauth2/login")
    public R oauthLogin(@RequestBody WBSocialUserTo user);


}
