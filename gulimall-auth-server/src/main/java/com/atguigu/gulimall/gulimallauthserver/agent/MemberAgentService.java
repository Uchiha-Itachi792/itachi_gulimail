package com.atguigu.gulimall.gulimallauthserver.agent;

import com.atguigu.common.to.WBSocialUserTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.gulimallauthserver.feign.MemberFeignService;
import com.atguigu.gulimall.gulimallauthserver.vo.WBSocialUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MemberAgentService {

    @Autowired
    MemberFeignService memberFeignService;

    public R oauthLogin(WBSocialUserVO user) {
        WBSocialUserTo param = new WBSocialUserTo();
        param.setAccessToken(user.getAccess_token());
        param.setExpiresIn(user.getExpires_in());
        param.setRemindIn(user.getRemind_in());
        param.setIsRealName(user.getIsRealName());
        param.setUid(user.getUid());
        return memberFeignService.oauthLogin(param);
    }
}
