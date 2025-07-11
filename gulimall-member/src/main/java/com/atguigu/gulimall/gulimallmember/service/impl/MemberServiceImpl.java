package com.atguigu.gulimall.gulimallmember.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.exception.PhoneException;
import com.atguigu.common.exception.UsernameException;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.gulimallmember.entity.MemberLevelEntity;
import com.atguigu.gulimall.gulimallmember.service.MemberLevelService;
import com.atguigu.gulimall.gulimallmember.vo.MemberUserRegisterVo;
import com.atguigu.gulimall.gulimallmember.vo.UserLoginVO;
import com.atguigu.gulimall.gulimallmember.vo.WBSocialUserVO;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.gulimallmember.dao.MemberDao;
import com.atguigu.gulimall.gulimallmember.entity.MemberEntity;
import com.atguigu.gulimall.gulimallmember.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 注册
     */
    @Override
    public void regist(MemberUserRegisterVo user) throws InterruptedException {
        MemberEntity entity = new MemberEntity();
        // 设置默认等级信息
        MemberLevelEntity level = memberLevelService.getDefaultLevel();
        entity.setLevelId(level.getId());

        // 校验手机号唯一、用户名唯一
        checkPhoneUnique(user.getPhone());
        checkUserNameUnique(user.getUserName());
        entity.setMobile(user.getPhone());
        entity.setUsername(user.getUserName());

        // 3.2.设置密码加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(user.getPassword());
        entity.setPassword(encode);
        entity.setCreateTime(new Date());


        baseMapper.insert(entity);
        // 1.加锁
//        RLock lock = redissonClient.getLock(MemberConstant.LOCK_KEY_REGIST_PRE + user.getPhone());
//        try {
//            lock.tryLock(30L, TimeUnit.SECONDS);
//            // 2.校验
//            // 校验手机号唯一、用户名唯一
//            checkPhoneUnique(user.getPhone());
//            checkUserNameUnique(user.getUserName());
//            // 3.封装保存
//            MemberEntity entity = new MemberEntity();
//            entity.setUsername(user.getUserName());
//            entity.setMobile(user.getPhone());
//            entity.setNickname(user.getUserName());
//            // 3.1.设置默认等级信息
//            MemberLevelEntity level = memberLevelService.getDefaultLevel();
//            entity.setLevelId(level.getId());
//            // 3.2.设置密码加密存储
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String encode = passwordEncoder.encode(user.getPassword());
//            entity.setPassword(encode);
//            entity.setCreateTime(new Date());
//            this.baseMapper.insert(entity);
//        } finally {
//            lock.unlock();
//        }
    }

    /**
     * 校验手机号是否唯一
     */
    public void checkPhoneUnique(String phone) throws PhoneException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>()
                .eq("mobile", phone));
        if (count > 0) {
            throw new PhoneException();
        }
    }

    /**
     * 校验用户名是否唯一
     */
    public void checkUserNameUnique(String userName) throws UsernameException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>()
                .eq("username", userName));
        if (count > 0) {
            throw new UsernameException();
        }
    }

    /**
     * 登录
     */
    @Override
    public MemberEntity login(UserLoginVO user) {
        String loginacct = user.getLoginacct();
        String password = user.getPassword();// 明文

        // 1.查询MD5密文
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct)
                .or()
                .eq("mobile", loginacct));
        if (entity != null) {
            // 2.获取password密文进行校验
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, entity.getPassword())) {
                // 登录成功
                return entity;
            }
        }
        // 3.登录失败
        return null;
    }

    @Override
    public MemberEntity login(WBSocialUserVO user) throws Exception {
        // 1.判断当前用户是否已经在本系统注册
        String uid = user.getUid();
        MemberEntity _entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("weibo_uid", user.getUid()));
        if (_entity != null) {
            // 2.已注册，直接返回
            MemberEntity member = new MemberEntity();
            member.setId(_entity.getId());
            member.setAccessToken(user.getAccess_token());
            member.setExpiresIn(user.getExpires_in());
            baseMapper.updateById(member);
            // 返回
            _entity.setAccessToken(user.getAccess_token());
            _entity.setExpiresIn(user.getExpires_in());
            return _entity;
        } else {
            // 3.未注册
            MemberEntity member = new MemberEntity();
            try {
                // 查询当前社交用户的社交账号信息，封装会员信息（查询结果不影响注册结果，所以使用try/catch）
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("access_token", user.getAccess_token());
                queryMap.put("uid", user.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", new HashMap<String, String>(), queryMap);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    String profileImageUrl = jsonObject.getString("profile_image_url");
                    // 封装注册信息
                    member.setNickname(name);
                    member.setGender("m".equals(gender) ? 1 : 0);
                    member.setHeader(profileImageUrl);
                    member.setCreateTime(new Date());
                }
            } catch (Exception e) {

            }
            member.setWeiboUid(user.getUid());
            member.setAccessToken(user.getAccess_token());
            member.setExpiresIn(user.getExpires_in());
            //把用户信息插入到数据库中
            baseMapper.insert(member);
            return member;
        }

    }


}