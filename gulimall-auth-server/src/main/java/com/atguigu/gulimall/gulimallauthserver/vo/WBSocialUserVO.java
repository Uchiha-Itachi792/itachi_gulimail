package com.atguigu.gulimall.gulimallauthserver.vo;

import lombok.Data;

@Data
public class WBSocialUserVO {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}

