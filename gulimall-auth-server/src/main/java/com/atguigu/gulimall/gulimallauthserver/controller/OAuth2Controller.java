package com.atguigu.gulimall.gulimallauthserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;

import com.atguigu.common.vo.MemberResponseVO;
import com.atguigu.gulimall.gulimallauthserver.agent.MemberAgentService;
import com.atguigu.gulimall.gulimallauthserver.vo.WBSocialUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
@Controller
@Slf4j
public class OAuth2Controller {


    @Autowired
    MemberAgentService memberAgentService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "2129105835"); //微博应用上的app key
        map.put("client_secret", "201b8aa95794dbb6d52ff914fc8954dc"); //微博应用上的app secret
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success"); //用户登录，确定授权后要跳转的页面
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", headers, querys, map);

        // 2.处理请求返回
        if (response.getStatusLine().getStatusCode() == 200) {
            // 换取Access_Token成功
            String jsonString = EntityUtils.toString(response.getEntity());
            WBSocialUserVO user = JSONObject.parseObject(jsonString, WBSocialUserVO.class);

            R r = memberAgentService.oauthLogin(user);
            if (r.getCode() == 0) {
                MemberResponseVO loginUser = r.getData(new TypeReference<MemberResponseVO>() {
                });
                session.setAttribute("loginUser", loginUser);
                log.info("登录成功：用户：{}", loginUser.toString());
                return "redirect:http://gulimall.com";
            }else {
                // 登录失败，调回登录页
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else {
            // 换取Access_Token成功
            return "redirect:http://auth.gulimall.com/login.html";

        }
    }
}
