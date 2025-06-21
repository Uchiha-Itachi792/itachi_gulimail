package com.atguigu.gulimall.gulimallmember.service;

import com.atguigu.common.exception.PhoneException;
import com.atguigu.common.exception.UsernameException;
import com.atguigu.gulimall.gulimallmember.vo.MemberUserRegisterVo;
import com.atguigu.gulimall.gulimallmember.vo.UserLoginVO;
import com.atguigu.gulimall.gulimallmember.vo.WBSocialUserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.gulimallmember.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author itachi
 * @email sunlightcs@gmail.com
 * @date 2024-09-16 15:34:26
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 注册
     */
    void regist(MemberUserRegisterVo user) throws InterruptedException;


    /**
     * 校验手机号是否唯一
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 校验用户名是否唯一
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    MemberEntity login(UserLoginVO user);

    MemberEntity login(WBSocialUserVO user) throws Exception ;
}

