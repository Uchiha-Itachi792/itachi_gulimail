package com.atguigu.common.to;

import lombok.Data;

@Data
public class WBSocialUserTo {
    // 微博
    private String accessToken;
    private String remindIn;
    private long expiresIn;
    private String uid;
    private String isRealName;

}

