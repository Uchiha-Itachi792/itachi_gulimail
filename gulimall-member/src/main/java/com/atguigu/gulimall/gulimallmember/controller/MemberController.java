package com.atguigu.gulimall.gulimallmember.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.WBSocialUserTo;
import com.atguigu.gulimall.gulimallmember.feign.CouponFeignService;
import com.atguigu.gulimall.gulimallmember.vo.MemberUserRegisterVo;
import com.atguigu.gulimall.gulimallmember.vo.UserLoginVO;
import com.atguigu.gulimall.gulimallmember.vo.WBSocialUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.gulimallmember.entity.MemberEntity;
import com.atguigu.gulimall.gulimallmember.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 会员
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-16 15:34:26
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    //test for openfeign
    @RequestMapping("/coupons")
    public R getCouponsByMemberId() {
        MemberEntity entity = new MemberEntity();
        entity.setId(1L);
        entity.setNickname("Alice");
        R coupons = couponFeignService.getCouponsByMemberId();
        return R.ok().put("Alice", entity).put("coupons", coupons);
    }


    /**
     * 注册
     */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberUserRegisterVo user) {
        try {
            memberService.regist(user);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("gulimallmember:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimallmember:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("gulimallmember:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimallmember:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("gulimallmember:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVO user) {
        try {
            MemberEntity entity = memberService.login(user);
            if (entity == null) {
                return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
            }
            return R.ok().setData(entity);
        } catch (Exception ex) {
            return R.error(ex.getMessage());
        }

    }

    /**
     * 微博社交登录
     */
    @PostMapping("/weibo/oauth2/login")
    public R oauthLogin(@RequestBody WBSocialUserVO user) {
        try {
            MemberEntity entity = memberService.login(user);
            return R.ok().setData(entity);
        } catch (Exception ex) {
            return R.error(ex.getMessage());
        }
    }

}
