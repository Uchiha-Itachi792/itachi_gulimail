package com.atguigu.gulimall.gulimallcart.interceptor;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.constant.auth.AuthConstant;
import com.atguigu.common.to.UserInfoTo;
import com.atguigu.common.vo.MemberResponseVO;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取会话信息，获取登录用户信息
        HttpSession session = request.getSession();
        MemberResponseVO attribute = (MemberResponseVO) session.getAttribute(AuthConstant.LOGIN_USER);
        // 判断是否登录，并封装User对象给controller使用
        UserInfoTo user = new UserInfoTo();
        if (attribute != null) {
            // 登录状态，封装用户ID，供controller使用
            user.setUserId(attribute.getId());
        }
        // 获取当前请求游客用户标识user-key
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    // 获取user-key值封装到user，供controller使用
                    user.setUserKey(cookie.getValue());
                    user.setTempUser(true);// 不需要重新分配
                    break;
                }
            }
        }

        // 判断当前是否存在游客用户标识
        if (StringUtils.isBlank(user.getUserKey())) {
            // 无游客标识，分配游客标识
            user.setUserKey(UUID.randomUUID().toString());
        }

        // 封装用户信息（登录状态userId非空，游客状态userId空）
        threadLocal.set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo user = threadLocal.get();
        if (user != null && !user.isTempUser()) {
            // 需要为客户端分配游客信息，key为user-key
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, user.getUserKey());
            cookie.setDomain("gulimall.com");// 作用域
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);// 过期时间
            response.addCookie(cookie);
        }
    }
}
